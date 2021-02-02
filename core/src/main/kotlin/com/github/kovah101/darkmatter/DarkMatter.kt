package com.github.kovah101.darkmatter

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.viewport.FitViewport
import com.github.kovah101.darkmatter.ecs.system.PlayerAnimationSystem
import com.github.kovah101.darkmatter.ecs.system.PlayerInputSystem
import com.github.kovah101.darkmatter.ecs.system.RenderSystem
import com.github.kovah101.darkmatter.screen.DarkMatterScreen
import com.github.kovah101.darkmatter.screen.GameScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.log.debug
import ktx.log.logger

private val LOG = logger<DarkMatter>()
const val UNIT_SCALE = 1 / 16f  // 1 world unit = 16 pixels

class DarkMatter : KtxGame<DarkMatterScreen>() {
    val gameViewport = FitViewport(9f, 16f) // world units
    val batch: Batch by lazy { SpriteBatch() }

    private val defaultRegion by lazy { TextureRegion(Texture(Gdx.files.internal("graphics/ship_base.png"))) }
    private val leftRegion by lazy { TextureRegion(Texture(Gdx.files.internal("graphics/ship_left.png"))) }
    private val rightRegion by lazy { TextureRegion(Texture(Gdx.files.internal("graphics/ship_right.png"))) }


    val engine: Engine by lazy {
        PooledEngine().apply {
            addSystem(PlayerInputSystem(gameViewport))
            addSystem(
                PlayerAnimationSystem(
                    defaultRegion,
                    leftRegion,
                    rightRegion
                )
            )
            addSystem(RenderSystem(batch, gameViewport))
        }
    }

    override fun create() {
        Gdx.app.logLevel = LOG_DEBUG
        LOG.debug { "Create game instance" }
        // must add screens before using
        addScreen(GameScreen(this))
        setScreen<GameScreen>()
    }

    override fun dispose() {
        super.dispose()
        LOG.debug { "Sprites in batch: ${(batch as SpriteBatch).maxSpritesInBatch}" }
        batch.dispose()

        defaultRegion.texture.dispose()
        leftRegion.texture.dispose()
        rightRegion.texture.dispose()
    }
}