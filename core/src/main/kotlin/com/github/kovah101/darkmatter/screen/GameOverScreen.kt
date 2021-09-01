package com.github.kovah101.darkmatter.screen

import com.badlogic.ashley.core.Engine
import com.github.kovah101.darkmatter.DarkMatter
import com.github.kovah101.darkmatter.assets.MusicAsset
import com.github.kovah101.darkmatter.ecs.system.DamageSystem
import com.github.kovah101.darkmatter.ecs.system.EnemySystem
import com.github.kovah101.darkmatter.ecs.system.PowerUpSystem
import com.github.kovah101.darkmatter.ecs.system.createEventHorizon
import com.github.kovah101.darkmatter.ui.GameOverUI
import ktx.actors.minusAssign
import ktx.actors.onChangeEvent
import ktx.actors.onClick
import ktx.actors.plusAssign
import ktx.ashley.getSystem
import ktx.log.debug
import ktx.log.logger
import ktx.preferences.flush
import ktx.preferences.set

private val LOG = logger<GameOverScreen>()

class GameOverScreen(
    game: DarkMatter,
    private val engine: Engine = game.engine
) : DarkMatterScreen(game) {
    private val ui = GameOverUI(bundle).apply {
        backButton.onClick {
            game.setScreen<MenuScreen>()
        }
        restartButton.onClick {
            game.setScreen<GameScreen>()

        }
    }

    var score = 0
    var highScore = 0

    override fun show() {
        //LOG.debug { "Game Over screen shown" }
        super.show()

        //start game over music
        audioService.play(MusicAsset.GAME_OVER)

        engine.createEventHorizon()

        setupUI()
    }

    private fun setupUI() {
        ui.run {
            updateScores(score, highScore)
            stage += this.table
        }
    }

    override fun hide() {
        engine.run {
            removeAllEntities()
            getSystem<PowerUpSystem>().setProcessing(true)
            getSystem<DamageSystem>().setProcessing(false)
            getSystem<EnemySystem>().setProcessing(true)
        }
        stage.clear()

    }

    override fun render(delta: Float) {
        engine.update(delta)
        audioService.update()
        stage.run {
            viewport.apply()
            act(delta)
            draw()
        }
    }
}