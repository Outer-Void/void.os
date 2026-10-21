package com.outervoid.voidgame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Graphics
import com.badlogic.gdx.ScreenAdapter
import java.lang.reflect.Proxy
import org.junit.Assert.*
import org.junit.Test

class VoidGameLifecycleTest {
    @Test fun transitionDisposesOnlyAfterOutgoingRenderAndForwardsLifecycle() {
        val previousGraphics = Gdx.graphics
        Gdx.graphics = Proxy.newProxyInstance(Graphics::class.java.classLoader, arrayOf(Graphics::class.java)) { _, method, _ ->
            when (method.name) {
                "getWidth" -> 360
                "getHeight" -> 640
                "getDeltaTime" -> 1f / 60f
                else -> null
            }
        } as Graphics
        try {
            val game = VoidGame()
            val events = mutableListOf<String>()
            val second = object : ScreenAdapter() {
                override fun show() { events += "show" }
                override fun pause() { events += "pause" }
                override fun resume() { events += "resume" }
                override fun dispose() { events += "dispose second" }
            }
            val first = object : ScreenAdapter() {
                override fun render(delta: Float) {
                    game.setScreen(second)
                    events += "render finished"
                }
                override fun dispose() { events += "dispose first" }
            }
            game.setScreen(first)
            game.render()
            game.render()
            game.pause()
            game.resume()
            game.dispose()
            assertEquals(listOf("render finished", "dispose first", "show", "pause", "resume", "dispose second"), events)
        } finally { Gdx.graphics = previousGraphics }
    }
}
