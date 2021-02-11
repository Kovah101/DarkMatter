package com.github.kovah101.darkmatter

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.viewport.FitViewport
import com.github.kovah101.darkmatter.assets.MusicAsset
import com.github.kovah101.darkmatter.assets.ShaderProgramAsset
import com.github.kovah101.darkmatter.assets.TextureAsset
import com.github.kovah101.darkmatter.assets.TextureAtlasAsset
import com.github.kovah101.darkmatter.audio.AudioService
import com.github.kovah101.darkmatter.audio.DefaultAudioService
import com.github.kovah101.darkmatter.ecs.system.*
import com.github.kovah101.darkmatter.event.GameEventManager
import com.github.kovah101.darkmatter.screen.DarkMatterScreen
import com.github.kovah101.darkmatter.screen.GameScreen
import com.github.kovah101.darkmatter.screen.LoadingScreen
import ktx.app.KtxGame
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.log.debug
import ktx.log.logger

private val LOG = logger<DarkMatter>()
const val UNIT_SCALE = 1 / 16f  // 1 world unit = 16 pixels
const val V_WIDTH = 9
const val V_HEIGHT = 16
const val V_WIDTH_PIXELS = 135
const val V_HEIGHT_PIXELS = 240

class DarkMatter : KtxGame<DarkMatterScreen>() {
    val uiViewport =
        FitViewport(V_WIDTH_PIXELS.toFloat(), V_HEIGHT_PIXELS.toFloat()) // different scale - more pixels- for UI
    val gameViewport = FitViewport(V_WIDTH.toFloat(), V_HEIGHT.toFloat()) // world units
    val batch: Batch by lazy { SpriteBatch() }
    val gameEventManager = GameEventManager()
    val assets: AssetStorage by lazy {
        KtxAsync.initiate()
        AssetStorage()
    }
    val audioService: AudioService by lazy { DefaultAudioService(assets) }
    val preferences : Preferences by lazy { Gdx.app.getPreferences("dark-matter")}

    val engine: Engine by lazy {


        PooledEngine().apply {
            val graphicsAtlas = assets[TextureAtlasAsset.GAME_GRAPHICS.descriptor]

            addSystem(PlayerInputSystem(gameViewport))
            addSystem(MoveSystem())
            addSystem(PowerUpSystem(gameEventManager, audioService))
            addSystem(DamageSystem(gameEventManager))
            addSystem(CameraShakeSystem(gameViewport.camera, gameEventManager))
            addSystem(
                PlayerAnimationSystem(
                    graphicsAtlas.findRegion("ship_base"),
                    graphicsAtlas.findRegion("ship_left"),
                    graphicsAtlas.findRegion("ship_right")
                )
            )
            addSystem(AttachSystem())
            addSystem(AnimationSystem(graphicsAtlas))
            addSystem(
                RenderSystem(
                    batch,
                    gameViewport,
                    uiViewport,
                    assets[TextureAsset.BACKGROUND.descriptor],
                    gameEventManager,
                    assets[ShaderProgramAsset.OUTLINE.descriptor]
                )
            )
            addSystem(RemoveSystem()) // last system to be added
            addSystem(DebugSystem())
        }
    }

    override fun create() {
        Gdx.app.logLevel = LOG_DEBUG
        LOG.debug { "Create game instance" }
        // must add screens before using
        addScreen(LoadingScreen(this))
        setScreen<LoadingScreen>()
//        addScreen(GameScreen(this))
//        setScreen<GameScreen>()
    }

    override fun dispose() {
        super.dispose()
        LOG.debug { "Sprites in batch: ${(batch as SpriteBatch).maxSpritesInBatch}" }
        MusicAsset.values().forEach {
            LOG.debug { "Refcount $it: ${assets.getReferenceCount(it.descriptor)}" }
        }
        batch.dispose()
        assets.dispose()
    }
}