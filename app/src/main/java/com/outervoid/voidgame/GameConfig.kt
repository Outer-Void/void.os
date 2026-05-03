package com.outervoid.voidgame

import com.badlogic.gdx.graphics.Color

object GameConfig {
    const val WORLD_WIDTH = 360f
    const val WORLD_HEIGHT = 640f
    const val CARRIER_SPEED = 220f
    const val BULLET_SPEED = 520f
    const val BULLET_COOLDOWN = 0.22f
    const val ENEMY_DESCEND_SPEED_BASE = 28f
    const val ENEMY_DESCEND_SPEED_SCALE = 4f
    const val DIVE_SPEED = 180f
    const val DIVE_CHANCE = 0.004f
    const val INTEGRITY_MAX = 100
    const val INTEGRITY_CONTACT_DAMAGE = 25
    const val INTEGRITY_BULLET_DAMAGE = 15
    const val PATCH_DROP_CHANCE = 0.10f

    val COLOR_BACKGROUND: Color = Color.valueOf("0B0B0BFF")
    val COLOR_AMBER: Color = Color.valueOf("FFD000FF")
    val COLOR_GRAY: Color = Color.valueOf("D8D8D8FF")
    val COLOR_DIM: Color = Color.valueOf("777777FF")
    val COLOR_SURFACE: Color = Color.valueOf("111111FF")
    val COLOR_DANGER: Color = Color.valueOf("E53935FF")
    val COLOR_ORANGE: Color = Color.valueOf("FF7A1AFF")
    val COLOR_ASTEROID: Color = Color.valueOf("24201CFF")
}
