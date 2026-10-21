package com.outervoid.voidgame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.FitViewport
import com.outervoid.voidgame.GameConfig
import com.outervoid.voidgame.ShooterVisuals
import com.outervoid.voidgame.ShooterRules
import com.outervoid.voidgame.ShooterControlState
import com.outervoid.voidgame.ShooterClock
import com.outervoid.voidgame.ShooterTouchRole
import com.outervoid.voidgame.shooterTouchRole
import com.outervoid.voidgame.VoidGame
import com.outervoid.voidgame.entities.Bullet
import com.outervoid.voidgame.entities.Carrier
import com.outervoid.voidgame.entities.EnemyBullet
import com.outervoid.voidgame.entities.Fragment
import com.outervoid.voidgame.entities.FragmentType
import com.outervoid.voidgame.entities.Patch
import com.outervoid.voidgame.entities.PatchType
import com.outervoid.voidgame.entities.VoidCore
import com.outervoid.voidgame.entities.SupportMissile

class GameScreen(
    private val game: VoidGame,
    private val survival: Boolean = false,
    private val campaignMission: Int = 0,
    private val campaignWaves: Int = 0
) : Screen {
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera)
    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()
    private val font = BitmapFont()
    private val layout = GlyphLayout()
    private val touch = Vector3()
    private val carrier = Carrier()
    private val bullets = Array(12) { Bullet() }
    private val enemyBullets = Array(8) { EnemyBullet() }
    private val fragments = mutableListOf<Fragment>()
    private val patches = mutableListOf<Patch>()
    private val particles = MutableList(72) { Particle() }
    private val rectA = Rectangle()
    private val rectB = Rectangle()
    private var voidCore: VoidCore? = null
    private var integrity = GameConfig.INTEGRITY_MAX
    private var signal = 0L
    private var cycle = 1
    private var elapsed = 0f
    private val clock = ShooterClock()
    private val controls = ShooterControlState()
    private var movementX: Float? = null
    private var finished = false
    private val reducedEffects = Gdx.app.getPreferences("axiom_shooter_settings").getBoolean("reduced_effects", false)
    private var isPaused = false
    private var backWasDown = false
    private var firewallActive = false
    private var firewallTimer = 0f
    private var overclockActive = false
    private var overclockTimer = 0f
    private var shakeTimer = 0f
    private var gridPulseTimer = 0f
    private var corePurgeTimer = 0f
    private var corePurgeX = 0f
    private var corePurgeY = 0f
    private val supportMissile = SupportMissile()
    private var missileFragment: Fragment? = null
    private var missileCore: VoidCore? = null
    private var missileCharges = 3
    private var bombCharges = 1
    private var movingPressed = false
    private var missilePressed = false
    private var bombPressed = false
    private val resumeBounds = Rectangle(92f, 292f, 176f, 42f)
    private val restartBounds = Rectangle(92f, 238f, 176f, 42f)
    private val exitBounds = Rectangle(92f, 184f, 176f, 42f)

    override fun show() {
        font.color = GameConfig.COLOR_GRAY
        font.data.setScale(1f)
        camera.setToOrtho(false, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT)
        spawnWave()
    }

    override fun render(delta: Float) {
        elapsed += ShooterRules.frameStep(delta)
        handleGlobalInput()
        if (!isPaused && !finished) {
            pollControls()
            if (!isPaused) clock.advance(delta) { step ->
                update(step)
                !finished && !isPaused
            }
        } else clock.reset()
        draw()
    }

    private fun handleGlobalInput() {
        val backDown = Gdx.input.isKeyPressed(Input.Keys.BACK)
        val newlyPressed = backDown && !backWasDown
        backWasDown = backDown
        if (newlyPressed) {
            pause()
            return
        }

        if (Gdx.input.justTouched()) {
            val world = worldTouch()
            if (isPaused) {
                when {
                    resumeBounds.contains(world.x, world.y) -> {
                        controls.quarantine()
                        isPaused = false
                    }
                    restartBounds.contains(world.x, world.y) -> game.setScreen(GameScreen(game, survival, campaignMission, campaignWaves))
                    exitBounds.contains(world.x, world.y) -> game.openAxiom()
                }
            }
        }
    }

    private fun pollControls() {
        for (pointer in 0 until controls.pointerCount) {
            val down = Gdx.input.isTouched(pointer)
            if (down) {
                touch.set(Gdx.input.getX(pointer).toFloat(), Gdx.input.getY(pointer).toFloat(), 0f)
                viewport.unproject(touch)
            }
            when (controls.update(pointer, down, touch.x, touch.y)) {
                ShooterTouchRole.Missile -> fireMissile()
                ShooterTouchRole.Bomb -> detonateBomb()
                ShooterTouchRole.Pause -> { pause(); return }
                else -> Unit
            }
        }
        val movementPointer = controls.movementPointer
        movementX = if (movementPointer >= 0) {
            touch.set(Gdx.input.getX(movementPointer).toFloat(), Gdx.input.getY(movementPointer).toFloat(), 0f)
            viewport.unproject(touch).x
        } else null
        movingPressed = movementPointer >= 0
        missilePressed = controls.isPressed(ShooterTouchRole.Missile)
        bombPressed = controls.isPressed(ShooterTouchRole.Bomb)
    }

    private fun update(delta: Float) {
        if (corePurgeTimer > 0f) {
            corePurgeTimer -= delta
            if (corePurgeTimer <= 0f) nextCycle()
            updateEffects(delta)
            return
        }

        carrier.update(delta, movementX)
        val fireScale = if (overclockActive) 0.48f else 1f
        carrier.tryFire(bullets)
        if (overclockActive && carrier.fireTimer > 0f) carrier.fireTimer -= delta * fireScale

        bullets.forEach { it.update(delta) }
        enemyBullets.forEach { it.update(delta) }
        patches.forEach { it.update(delta) }
        updatePowerups(delta)
        updateFragments(delta)
        updateVoidCore(delta)
        updateMissile(delta)
        updateCollisions()
        updateEffects(delta)
        patches.removeAll { !it.active }
        fragments.removeAll { !it.active }

        if (integrity <= 0) {
            finished = true
            game.setScreen(ScoreScreen(game, signal, cycle, survival, campaignMission, campaignWaves))
            return
        }
        if (fragments.none { it.active } && voidCore?.active != true && corePurgeTimer <= 0f) nextCycle()
    }

    private fun updatePowerups(delta: Float) {
        if (firewallActive) {
            firewallTimer -= delta
            if (firewallTimer <= 0f) firewallActive = false
        }
        if (overclockActive) {
            overclockTimer -= delta
            if (overclockTimer <= 0f) overclockActive = false
        }
    }

    private fun updateFragments(delta: Float) {
        fragments.forEach { fragment ->
            val wasActive = fragment.active
            val shouldFire = fragment.update(delta, cycle, carrier.x + carrier.width * 0.5f, carrier.y)
            if (shouldFire) spawnEnemyBullet(fragment.x, fragment.y - 12f)
            if (wasActive && !fragment.active && fragment.y < 0f) {
                hitIntegrity(GameConfig.INTEGRITY_CONTACT_DAMAGE)
            }
        }
    }

    private fun updateVoidCore(delta: Float) {
        val core = voidCore ?: return
        if (!core.active) return
        if (core.update(delta)) {
            spawnEnemyBullet(core.x, core.y - 24f, -70f)
            spawnEnemyBullet(core.x, core.y - 28f, 0f)
            spawnEnemyBullet(core.x, core.y - 24f, 70f)
        }
    }

    private fun updateCollisions() {
        bullets.forEach { bullet ->
            if (!bullet.active) return@forEach
            val bulletRect = bullet.bounds(rectA)
            fragments.firstOrNull { it.active && overlaps(bulletRect, it.bounds(rectB)) }?.let { fragment ->
                bullet.active = false
                fragment.hp -= 1
                if (fragment.hp <= 0) destroyFragment(fragment)
            }
            val core = voidCore
            if (bullet.active && core?.active == true && overlaps(bulletRect, core.bounds(rectB))) {
                bullet.active = false
                core.hp -= 1
                if (core.hp <= 0) purgeCore(core)
            }
        }

        val carrierRect = carrier.bounds(rectA)
        enemyBullets.forEach { bullet ->
            if (bullet.active && overlaps(carrierRect, bullet.bounds(rectB))) {
                bullet.active = false
                if (!firewallActive) hitIntegrity(GameConfig.INTEGRITY_BULLET_DAMAGE)
            }
        }
        fragments.forEach { fragment ->
            if (fragment.active && overlaps(carrierRect, fragment.bounds(rectB))) {
                fragment.active = false
                hitIntegrity(GameConfig.INTEGRITY_CONTACT_DAMAGE)
                burst(fragment.x, fragment.y)
            }
        }
        patches.forEach { patch ->
            if (patch.active && overlaps(carrierRect, patch.bounds(rectB))) {
                patch.active = false
                applyPatch(patch.type)
            }
        }
    }

    private fun destroyFragment(fragment: Fragment) {
        fragment.active = false
        signal += fragment.type.signal.toLong()
        burst(fragment.x, fragment.y)
        if (fragment.type == FragmentType.CLUSTER) {
            fragments += Fragment((fragment.x - 20f).coerceAtLeast(18f), fragment.y, FragmentType.SHARD)
            fragments += Fragment((fragment.x + 20f).coerceAtMost(GameConfig.WORLD_WIDTH - 18f), fragment.y, FragmentType.SHARD)
        }
        if (MathUtils.random() < GameConfig.PATCH_DROP_CHANCE) {
            patches += Patch(fragment.x, fragment.y, PatchType.values()[MathUtils.random(0, PatchType.values().lastIndex)])
        }
    }

    private fun overlaps(a: Rectangle, b: Rectangle): Boolean =
        ShooterRules.overlaps(a.x, a.y, a.width, a.height, b.x, b.y, b.width, b.height)

    private fun purgeCore(core: VoidCore) {
        core.active = false
        signal += 500
        corePurgeX = core.x
        corePurgeY = core.y
        corePurgeTimer = 0.6f
        burst(core.x, core.y, 24)
    }

    private fun hitIntegrity(amount: Int) {
        if (carrier.damageTimer > 0f || firewallActive) return
        integrity = (integrity - amount).coerceAtLeast(0)
        shakeTimer = 0.15f
        gridPulseTimer = 0.3f
        carrier.registerHit()
        burst(carrier.x + carrier.width * 0.5f, carrier.y + carrier.height * 0.6f, 10)
    }

    private fun fireMissile() {
        if (supportMissile.active || missileCharges <= 0) return
        var target: Fragment? = null
        var nearest = Float.MAX_VALUE
        for (fragment in fragments) {
            if (!fragment.active) continue
            val distance = kotlin.math.abs(fragment.x - carrier.x) + kotlin.math.abs(fragment.y - carrier.y)
            if (distance < nearest) { nearest = distance; target = fragment }
        }
        val core = voidCore?.takeIf { it.active }
        if (target == null && core == null) return
        if (!supportMissile.launch(carrier.x + carrier.width * 0.5f, carrier.y + carrier.height)) return
        missileCharges = ShooterRules.useWeaponCharge(missileCharges) ?: return
        missileFragment = target
        missileCore = if (target == null) core else null
    }

    private fun updateMissile(delta: Float) {
        if (!supportMissile.active) return
        val target = missileFragment?.takeIf { it.active }
        val core = missileCore?.takeIf { it.active }
        if (target == null && core == null) {
            supportMissile.cancel()
            return
        }
        val x = target?.x ?: core!!.x
        val y = target?.y ?: core!!.y
        if (!supportMissile.advance(delta, x, y)) return
        burst(x, y, 14)
        if (target != null) {
            target.hp -= 2
            if (target.hp <= 0) destroyFragment(target)
        } else if (core != null) {
            core.hp -= 2
            if (core.hp <= 0) purgeCore(core)
        }
        missileFragment = null
        missileCore = null
    }

    private fun detonateBomb() {
        if (fragments.none { it.active } && voidCore?.active != true && enemyBullets.none { it.active }) return
        bombCharges = ShooterRules.useWeaponCharge(bombCharges) ?: return
        enemyBullets.forEach { it.active = false }
        fragments.forEach { fragment ->
            if (!fragment.active) return@forEach
            fragment.active = false
            signal += fragment.type.signal.toLong()
            burst(fragment.x, fragment.y, 10)
        }
        voidCore?.takeIf { it.active }?.let { core ->
            core.hp -= 12
            burst(core.x, core.y, 22)
            if (core.hp <= 0) purgeCore(core)
        }
        gridPulseTimer = 0.65f
    }

    private fun applyPatch(type: PatchType) {
        when (type) {
            PatchType.FIREWALL -> {
                firewallActive = true
                firewallTimer = 7f
            }
            PatchType.OVERCLOCK -> {
                overclockActive = true
                overclockTimer = 6f
            }
            PatchType.DEFRAG -> integrity = (integrity + 20).coerceAtMost(GameConfig.INTEGRITY_MAX)
        }
    }

    private fun nextCycle() {
        if (campaignMission > 0 && ShooterRules.campaignMissionComplete(cycle, campaignWaves)) {
            completeCampaignMission()
            return
        }
        cycle += 1
        spawnWave()
    }

    private fun completeCampaignMission() {
        val prefs = Gdx.app.getPreferences("axiom_shooter")
        val done = prefs.getInteger("missions_complete", 0)
        if (campaignMission > done) {
            prefs.putInteger("missions_complete", campaignMission)
            prefs.flush()
        }
        finished = true
        game.setScreen(ScoreScreen(game, signal, cycle, survival, campaignMission, campaignWaves, missionComplete = true))
    }

    private fun spawnWave() {
        supportMissile.cancel()
        missileFragment = null
        missileCore = null
        fragments.clear()
        patches.clear()
        enemyBullets.forEach { it.active = false }
        voidCore = null
        if (cycle % 5 == 0) {
            voidCore = VoidCore()
            for (i in 0 until 6) fragments += Fragment(60f + i * 48f, 690f + i * 6f, FragmentType.SHARD)
            return
        }
        val cols = 5
        val rows = if (survival) (3 + cycle / 5).coerceAtMost(5) else 3
        var index = 0
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                var type = FragmentType.SHARD
                if (cycle >= 2 && index < (cycle * 2).coerceAtMost(5)) type = FragmentType.PULSE
                if (cycle >= 3 && index in 8..(8 + cycle.coerceAtMost(2))) type = FragmentType.GLITCH
                if (cycle >= 4 && index in 12..13) type = FragmentType.CLUSTER
                fragments += Fragment(56f + col * 62f, 680f + row * 42f, type)
                index += 1
            }
        }
    }

    private fun spawnEnemyBullet(x: Float, y: Float, vx: Float = 0f) {
        enemyBullets.firstOrNull { !it.active }?.spawn(x, y, vx)
    }

    private fun burst(x: Float, y: Float, count: Int = MathUtils.random(5, 8)) {
        repeat(if (reducedEffects) minOf(count, 3) else count) {
            val particle = particles.firstOrNull { !it.active } ?: return@repeat
            val angle = MathUtils.random(0f, MathUtils.PI2)
            val speed = MathUtils.random(45f, 135f)
            particle.spawn(x, y, MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed)
        }
    }

    private fun updateEffects(delta: Float) {
        particles.forEach { it.update(delta) }
        if (shakeTimer > 0f) shakeTimer -= delta
        if (gridPulseTimer > 0f) gridPulseTimer -= delta
    }

    private fun draw() {
        val shakeX = if (!reducedEffects && shakeTimer > 0f) MathUtils.random(-6f, 6f) else 0f
        val shakeY = if (!reducedEffects && shakeTimer > 0f) MathUtils.random(-6f, 6f) else 0f
        camera.position.set(GameConfig.WORLD_WIDTH * 0.5f + shakeX, GameConfig.WORLD_HEIGHT * 0.5f + shakeY, 0f)
        camera.update()
        shapeRenderer.projectionMatrix = camera.combined
        batch.projectionMatrix = camera.combined

        Gdx.gl.glClearColor(GameConfig.COLOR_BACKGROUND.r, GameConfig.COLOR_BACKGROUND.g, GameConfig.COLOR_BACKGROUND.b, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        drawShapes()
        // Combat shake must not move labels, pause actions, or input unprojection.
        camera.position.set(GameConfig.WORLD_WIDTH * 0.5f, GameConfig.WORLD_HEIGHT * 0.5f, 0f)
        camera.update()
        shapeRenderer.projectionMatrix = camera.combined
        batch.projectionMatrix = camera.combined
        drawHud()
        if (isPaused) drawPauseOverlay()
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }

    private fun drawShapes() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        ShooterVisuals.drawSpaceBackdrop(shapeRenderer, elapsed)
        carrier.drawHull(shapeRenderer)
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        ShooterVisuals.drawSpaceLineDetails(shapeRenderer, elapsed)
        drawCombatFrame()
        drawGrid()
        bullets.forEach { it.draw(shapeRenderer) }
        enemyBullets.forEach { it.draw(shapeRenderer) }
        if (supportMissile.active) {
            shapeRenderer.color = GameConfig.COLOR_GRAY
            shapeRenderer.rectLine(supportMissile.x, supportMissile.y - 5f, supportMissile.x, supportMissile.y + 9f, 4f)
            shapeRenderer.color = GameConfig.COLOR_AMBER
            shapeRenderer.line(supportMissile.x, supportMissile.y - 6f, supportMissile.x, supportMissile.y - 17f)
        }
        fragments.forEach { it.draw(shapeRenderer, elapsed) }
        voidCore?.draw(shapeRenderer)
        carrier.draw(shapeRenderer)
        particles.forEach { it.draw(shapeRenderer) }
        drawIntegrityBar()
        drawControlOverlay()
        if (firewallActive) drawFirewallEdges()
        patches.forEach { it.draw(shapeRenderer) }
        shapeRenderer.end()
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        if (overclockActive && !reducedEffects) drawTint(0.04f)
        if (corePurgeTimer > 0f && !reducedEffects) drawCorePurge()
        shapeRenderer.end()
    }

    private fun drawGrid() {
        val alpha = if (gridPulseTimer > 0f) 0.08f else 0.03f
        ShooterVisuals.setAlphaColor(shapeRenderer, GameConfig.COLOR_AMBER, alpha)
        var x = 0f
        while (x <= GameConfig.WORLD_WIDTH) {
            shapeRenderer.line(x, 116f, x, GameConfig.WORLD_HEIGHT - 70f)
            x += 20f
        }
        var y = 120f
        while (y <= GameConfig.WORLD_HEIGHT - 70f) {
            shapeRenderer.line(0f, y, GameConfig.WORLD_WIDTH, y)
            y += 20f
        }
    }

    private fun drawCombatFrame() {
        ShooterVisuals.setAlphaColor(shapeRenderer, GameConfig.COLOR_DIM, 0.38f)
        ShooterVisuals.drawCornerBrackets(shapeRenderer, 12f, 12f, GameConfig.WORLD_WIDTH - 24f, GameConfig.WORLD_HEIGHT - 24f, 18f)
        ShooterVisuals.drawPanelFrame(shapeRenderer, 12f, GameConfig.WORLD_HEIGHT - 62f, 102f, 42f)
        ShooterVisuals.drawPanelFrame(shapeRenderer, GameConfig.WORLD_WIDTH - 114f, GameConfig.WORLD_HEIGHT - 62f, 102f, 42f)
        ShooterVisuals.setAlphaColor(shapeRenderer, GameConfig.COLOR_AMBER, 0.5f)
        shapeRenderer.line(136f, GameConfig.WORLD_HEIGHT - 42f, 224f, GameConfig.WORLD_HEIGHT - 42f)
    }

    private fun drawIntegrityBar() {
        val left = 22f
        val right = GameConfig.WORLD_WIDTH - 22f
        val y = 96f
        ShooterVisuals.setAlphaColor(shapeRenderer, GameConfig.COLOR_DIM, 0.5f)
        shapeRenderer.rect(left, y - 4f, right - left, 8f)
        val segments = 20
        val activeSegments = (segments * (integrity / GameConfig.INTEGRITY_MAX.toFloat())).toInt()
        for (i in 0 until segments) {
            shapeRenderer.color = when {
                i >= activeSegments -> GameConfig.COLOR_SURFACE
                integrity <= 25 -> GameConfig.COLOR_DANGER
                else -> GameConfig.COLOR_AMBER
            }
            val sx = left + 4f + i * ((right - left - 8f) / segments)
            shapeRenderer.line(sx, y, sx + 8f, y)
        }
    }

    private fun drawControlOverlay() {
        ShooterVisuals.drawPanelFrame(shapeRenderer, 16f, 24f, 192f, 48f)
        ShooterVisuals.drawControlRing(shapeRenderer, 252f, 42f, 27f, missilePressed)
        ShooterVisuals.drawControlRing(shapeRenderer, 324f, 42f, 27f, bombPressed)
    }

    private fun drawFirewallEdges() {
        ShooterVisuals.setAlphaColor(shapeRenderer, GameConfig.COLOR_DIM, 0.45f)
        shapeRenderer.rect(8f, 8f, GameConfig.WORLD_WIDTH - 16f, GameConfig.WORLD_HEIGHT - 16f)
        shapeRenderer.rect(12f, 12f, GameConfig.WORLD_WIDTH - 24f, GameConfig.WORLD_HEIGHT - 24f)
    }

    private fun drawTint(alpha: Float) {
        ShooterVisuals.setAlphaColor(shapeRenderer, GameConfig.COLOR_AMBER, alpha)
        shapeRenderer.rect(0f, 0f, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT)
    }

    private fun drawCorePurge() {
        val progress = 1f - (corePurgeTimer / 0.6f)
        ShooterVisuals.setAlphaColor(shapeRenderer, GameConfig.COLOR_AMBER, 0.22f * (1f - progress))
        shapeRenderer.circle(corePurgeX, corePurgeY, 40f + progress * 320f, 64)
        shapeRenderer.circle(corePurgeX, corePurgeY, 16f + progress * 160f, 48)
    }

    private fun drawHud() {
        batch.begin()
        font.data.setScale(0.9f)
        font.color = GameConfig.COLOR_GRAY
        font.draw(batch, "SCORE", 22f, GameConfig.WORLD_HEIGHT - 30f)
        font.color = GameConfig.COLOR_AMBER
        font.draw(batch, signal.toString(), 22f, GameConfig.WORLD_HEIGHT - 48f)
        font.color = GameConfig.COLOR_GRAY
        drawRight("WAVE", GameConfig.WORLD_WIDTH - 22f, GameConfig.WORLD_HEIGHT - 30f)
        font.color = GameConfig.COLOR_AMBER
        drawRight(cycle.toString(), GameConfig.WORLD_WIDTH - 22f, GameConfig.WORLD_HEIGHT - 48f)
        font.color = GameConfig.COLOR_GRAY
        font.draw(batch, "INTEGRITY", 22f, 82f)
        drawRight("$integrity%", GameConfig.WORLD_WIDTH - 22f, 82f)
        font.data.setScale(0.72f)
        font.color = if (movingPressed) GameConfig.COLOR_AMBER else GameConfig.COLOR_GRAY
        font.draw(batch, "DRAG TO MOVE", 30f, 54f)
        font.data.setScale(0.62f)
        font.draw(batch, "AUTO FIRE", 30f, 36f)
        font.color = if (missileCharges > 0) GameConfig.COLOR_AMBER else GameConfig.COLOR_DIM
        font.draw(batch, "MSL $missileCharges", 233f, 47f)
        font.color = if (bombCharges > 0) GameConfig.COLOR_AMBER else GameConfig.COLOR_DIM
        font.draw(batch, "BMB $bombCharges", 304f, 47f)
        font.color = GameConfig.COLOR_GRAY
        drawCentered("PAUSE", 180f, GameConfig.WORLD_HEIGHT - 52f)
        drawPowerups()
        if (corePurgeTimer > 0f) {
            font.color = GameConfig.COLOR_AMBER
            font.data.setScale(1.25f)
            drawCentered("VOID CORE PURGED", GameConfig.WORLD_WIDTH * 0.5f, GameConfig.WORLD_HEIGHT * 0.52f)
        }
        batch.end()
    }

    private fun drawPowerups() {
        var x = 134f
        font.data.setScale(0.72f)
        if (firewallActive) {
            font.color = GameConfig.COLOR_DIM
            font.draw(batch, "FW", x, GameConfig.WORLD_HEIGHT - 20f)
            font.color = GameConfig.COLOR_AMBER
            font.draw(batch, firewallTimer.toInt().toString(), x, GameConfig.WORLD_HEIGHT - 34f)
            x += 36f
        }
        if (overclockActive) {
            font.color = GameConfig.COLOR_DIM
            font.draw(batch, "OC", x, GameConfig.WORLD_HEIGHT - 20f)
            font.color = GameConfig.COLOR_AMBER
            font.draw(batch, overclockTimer.toInt().toString(), x, GameConfig.WORLD_HEIGHT - 34f)
        }
    }

    private fun drawPauseOverlay() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        ShooterVisuals.setAlphaColor(shapeRenderer, GameConfig.COLOR_BACKGROUND, 0.78f)
        shapeRenderer.rect(0f, 0f, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT)
        shapeRenderer.end()
        batch.begin()
        font.data.setScale(1.7f)
        font.color = GameConfig.COLOR_AMBER
        drawCentered("PAUSED", GameConfig.WORLD_WIDTH * 0.5f, 366f)
        font.data.setScale(1.1f)
        drawCentered("[ RESUME ]", GameConfig.WORLD_WIDTH * 0.5f, 314f)
        font.color = GameConfig.COLOR_GRAY
        drawCentered("[ RESTART ]", GameConfig.WORLD_WIDTH * 0.5f, 260f)
        drawCentered("[ EXIT TO AXIOM ]", GameConfig.WORLD_WIDTH * 0.5f, 206f)
        batch.end()
    }

    private fun drawCentered(text: String, centerX: Float, y: Float) {
        layout.setText(font, text)
        font.draw(batch, layout, centerX - layout.width * 0.5f, y)
    }

    private fun drawRight(text: String, rightX: Float, y: Float) {
        layout.setText(font, text)
        font.draw(batch, layout, rightX - layout.width, y)
    }

    private fun worldTouch(): Vector3 {
        touch.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        viewport.unproject(touch)
        return touch
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun pause() {
        isPaused = true
        clock.reset()
        controls.quarantine()
        movementX = null
        movingPressed = false
        missilePressed = false
        bombPressed = false
    }

    override fun resume() = Unit
    override fun hide() = Unit

    override fun dispose() {
        shapeRenderer.dispose()
        batch.dispose()
        font.dispose()
    }

    private class Particle {
        var active = false
        var x = 0f
        var y = 0f
        var vx = 0f
        var vy = 0f
        var life = 0f

        fun spawn(startX: Float, startY: Float, velocityX: Float, velocityY: Float) {
            x = startX
            y = startY
            vx = velocityX
            vy = velocityY
            life = 0.4f
            active = true
        }

        fun update(delta: Float) {
            if (!active) return
            life -= delta
            x += vx * delta
            y += vy * delta
            if (life <= 0f) active = false
        }

        fun draw(renderer: ShapeRenderer) {
            if (!active) return
            val progress = (life / 0.4f).coerceIn(0f, 1f)
            ShooterVisuals.setAlphaColor(renderer, GameConfig.COLOR_AMBER, progress)
            renderer.rect(x - 1.2f * progress, y - 1.2f * progress, 2.4f * progress, 2.4f * progress)
        }
    }
}
