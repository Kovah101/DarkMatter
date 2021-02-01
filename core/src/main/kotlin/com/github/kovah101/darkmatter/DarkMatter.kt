package com.github.kovah101.darkmatter

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.github.kovah101.darkmatter.screen.DarkMatterScreen
import com.github.kovah101.darkmatter.screen.GameScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.log.debug
import ktx.log.logger

private val LOG = logger<DarkMatter>()
const val UNIT_SCALE = 1/16f  // 1 world unit = 16 pixels

class DarkMatter : KtxGame<DarkMatterScreen>() {
    val batch: Batch by lazy {SpriteBatch()  }
    val engine: Engine by lazy { PooledEngine() }

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
    }
}