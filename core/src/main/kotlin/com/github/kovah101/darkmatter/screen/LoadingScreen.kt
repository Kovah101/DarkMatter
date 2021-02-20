package com.github.kovah101.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.github.kovah101.darkmatter.DarkMatter
import com.github.kovah101.darkmatter.assets.ShaderProgramAsset
import com.github.kovah101.darkmatter.assets.SoundAsset
import com.github.kovah101.darkmatter.assets.TextureAsset
import com.github.kovah101.darkmatter.assets.TextureAtlasAsset
import com.github.kovah101.darkmatter.ui.LabelStyles
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.actors.plus
import ktx.actors.plusAssign
import ktx.async.KtxAsync
import ktx.collections.gdxArrayOf
import ktx.log.debug
import ktx.log.logger
import ktx.scene2d.*

private val LOG = logger<LoadingScreen>()

class LoadingScreen(game: DarkMatter) : DarkMatterScreen(game) {
    private lateinit var progressBar: Image
    private lateinit var touchToBeginLabel: Label

    override fun show() {
        LOG.debug { "Loading screen shown" }
        val old = System.currentTimeMillis()
        // queue asset loading
        val assetRefs = gdxArrayOf(
            TextureAsset.values().map { assets.loadAsync(it.descriptor) },
            TextureAtlasAsset.values().map { assets.loadAsync(it.descriptor) },
            SoundAsset.values().map { assets.loadAsync(it.descriptor) },
            ShaderProgramAsset.values().map { assets.loadAsync(it.descriptor) }
        ).flatten()

        // once assets are loaded change to GameScreen
        // async so non blocking main thread
        KtxAsync.launch {
            assetRefs.joinAll()
            LOG.debug { "Time to load assets: ${System.currentTimeMillis() - old}ms" }
            assetsLoaded()
        }

        // setup UI for loading screen
        setupUi()
    }

    override fun hide() {
        stage.clear()
    }

    private fun setupUi() {
        stage.actors {
            table {
                defaults().fillX().expandX()

                label("Loading Screen", LabelStyles.GRADIENT.name) {
                    wrap = true
                    setAlignment(Align.center)
                }
                row()

                touchToBeginLabel = label("Touch To Begin", LabelStyles.DEFAULT.name) {
                    wrap = true
                    setAlignment(Align.center)
                    color.a = 0f
                }
                row()

                stack { cell ->
                    progressBar = image("life_bar").apply {
                        scaleX = 0f
                    }
                    label("Loading...", LabelStyles.DEFAULT.name) {
                        setAlignment(Align.center)
                    }
                    cell.padLeft(5f).padRight(5f)
                }

                setFillParent(true)
                pack()
            }
        }
        // stage.isDebugAll = true
    }

    override fun render(delta: Float) {
        if (assets.progress.isFinished && Gdx.input.justTouched() && game.containsScreen<GameScreen>()) {
            game.setScreen<MenuScreen>()
            game.removeScreen<LoadingScreen>()
            dispose()
        }

        // increase length of progress bar as loading progresses
        progressBar.scaleX = assets.progress.percent
        stage.run {
            viewport.apply()
            act()
            draw()
        }
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    private fun assetsLoaded() {
        game.addScreen(MenuScreen(game))
        game.addScreen(GameScreen(game))
        game.addScreen(GameOverScreen(game))
        touchToBeginLabel += Actions.forever(
            Actions.sequence(
                Actions.fadeIn(0.5f)
                        + Actions.fadeOut(0.5f)
            )
        )

    }

}