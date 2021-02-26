package com.github.kovah101.darkmatter.screen

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.github.kovah101.darkmatter.DarkMatter
import com.github.kovah101.darkmatter.assets.MusicAsset
import com.github.kovah101.darkmatter.ecs.system.*
import com.github.kovah101.darkmatter.ui.ControlDialog
import com.github.kovah101.darkmatter.ui.MenuUI
import com.github.kovah101.darkmatter.ui.TextDialog
import ktx.actors.onChangeEvent
import ktx.actors.onClick
import ktx.actors.plusAssign
import ktx.ashley.getSystem
import ktx.log.debug
import ktx.log.logger
import ktx.preferences.flush
import ktx.preferences.set
import ktx.preferences.get

private val LOG = logger<MenuScreen>()

class MenuScreen(
    game: DarkMatter,
    private val engine: Engine = game.engine
) : DarkMatterScreen(game) {
    private val ui = MenuUI(bundle).apply {
        startGameButton.onClick { game.setScreen<GameScreen>() }
        soundButton.onChangeEvent {
            audioService.enabled = !this.isChecked
            preferences.flush {
                this["musicEnabledKey"] = audioService.enabled
            }
            LOG.debug { "audio mode = ${audioService.enabled}" }
            if(audioService.enabled) { audioService.play(MusicAsset.MENU) }

        }
        quitGameButton.onClick { Gdx.app.exit() }
        creditsButton.onClick {
            creditsDialog.show(stage)
        }
        controlButton.onClick {
            controlDialog.show(stage)

        }
    }

    private val creditsDialog = TextDialog(bundle, "credits")
    private val controlDialog = ControlDialog(bundle)

    override fun show() {
        super.show()
        engine.run {
            createPlayer(assets, spawnY = 2.8f)
            createEventHorizon()
            getSystem<PowerUpSystem>().setProcessing(true)
        }
        audioService.enabled = preferences["musicEnabledKey", true]
        LOG.debug { "audio mode starts = ${audioService.enabled}" }
        audioService.play(MusicAsset.MENU)
        setupUI()
    }

    private fun setupUI(){
        ui.run {
            soundButton.isChecked = !audioService.enabled
            updateHighScore(preferences["highscore", 0])
            stage += this.table
        }
        //stage.isDebugAll = true
    }

    override fun hide() {
        super.hide()
        engine.run {
            removeAllEntities()
            getSystem<PowerUpSystem>().setProcessing(true)
            getSystem<DamageSystem>().setProcessing(true)
            getSystem<EnemySystem>().setProcessing(true)
        }
        stage.clear()
    }

    override fun render(delta: Float) {
        engine.update(delta)
        //audioService.play(MusicAsset.MENU)

        stage.run {
            viewport.apply()
            act(delta)
            draw()
        }
    }

}