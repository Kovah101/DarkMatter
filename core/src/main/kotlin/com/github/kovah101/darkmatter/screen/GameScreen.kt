package com.github.kovah101.darkmatter.screen


import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.github.kovah101.darkmatter.DarkMatter
import com.github.kovah101.darkmatter.UNIT_SCALE
import com.github.kovah101.darkmatter.V_WIDTH
import com.github.kovah101.darkmatter.assets.MusicAsset
import com.github.kovah101.darkmatter.ecs.components.*
import com.github.kovah101.darkmatter.ecs.system.DAMAGE_AREA_HEIGHT
import com.github.kovah101.darkmatter.ecs.system.createDarkMatter
import com.github.kovah101.darkmatter.ecs.system.createPlayer
import com.github.kovah101.darkmatter.event.GameEvent
import com.github.kovah101.darkmatter.event.GameEventListener
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import ktx.log.debug
import ktx.log.logger
import ktx.preferences.flush
import ktx.preferences.get
import ktx.preferences.set
import kotlin.math.min
import kotlin.math.roundToInt

private val LOG = logger<GameScreen>()
private const val MAX_DELTA_TIME = 1 / 20f //used to stop spiral of death
var playerAlive : Boolean = false

class GameScreen(
    game: DarkMatter,
    private val engine: Engine = game.engine
) : DarkMatterScreen(game), GameEventListener {

    override fun show() {
        LOG.debug { "Game screen shown" }
        LOG.debug { "High Score: ${preferences["highscore", 0]}" }
        gameEventManager.addListener(GameEvent.PlayerDeath::class, this)

        audioService.play(MusicAsset.GAME)

        engine.run {
            // attempt to stop constant player respawning
            LOG.debug { "player alive = $playerAlive" }
            if (!playerAlive) {
                createPlayer(assets)
                createDarkMatter()
                playerAlive = true
            }
        }
        //spawnPlayer()

        // dark matter
        //spawnDarkMatter()
    }


    override fun hide() {
        super.hide()
        gameEventManager.removeListener(this)
    }


    override fun render(delta: Float) {
        engine.update(min(MAX_DELTA_TIME, delta))
        audioService.update()
    }

    override fun onEvent(event: GameEvent) {
        when (event) {
            is GameEvent.PlayerDeath -> {
                playerAlive = false
                val distanceScore = event.distance.roundToInt()
                LOG.debug { "Player died with a distance of $distanceScore " }
                // store high score
                if (distanceScore > preferences["highscore", 0]) {
                    preferences.flush {
                        this["highscore"] = distanceScore
                    }
                }
                game.getScreen<GameOverScreen>().run {
                    score = distanceScore
                    highScore = preferences["highscore", 0]
                }
                game.setScreen<GameOverScreen>()
            }
            GameEvent.CollectPowerUp -> TODO()
        }

    }
}


