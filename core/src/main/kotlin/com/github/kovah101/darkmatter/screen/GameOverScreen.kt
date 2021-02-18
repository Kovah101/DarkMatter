package com.github.kovah101.darkmatter.screen

import com.badlogic.ashley.core.Engine
import com.github.kovah101.darkmatter.DarkMatter
import com.github.kovah101.darkmatter.ui.GameOverUI
import ktx.actors.minusAssign
import ktx.actors.onClick
import ktx.actors.plusAssign
import ktx.log.debug
import ktx.log.logger

private val LOG = logger<GameOverScreen>()

class GameOverScreen(
    game: DarkMatter,
    private val engine: Engine = game.engine
) : DarkMatterScreen(game) {
    private val ui = GameOverUI(bundle).apply {
        backButton.onClick {
            game.setScreen<GameScreen>()
            // hide UI
            stage -= table
        }
    }

    var score = 0
    var highScore = 0

    override fun show() {
        LOG.debug { "Game Over screen shown" }
        super.show()

        ui.run {
            updateScores(score, highScore)
            stage += this.table
        }
    }

    override fun render(delta: Float) {
        engine.update(delta)
        stage.run {
            viewport.apply()
            act(delta)
            draw()
        }
    }
}