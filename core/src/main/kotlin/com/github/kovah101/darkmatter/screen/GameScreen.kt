package com.github.kovah101.darkmatter.screen


import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.github.kovah101.darkmatter.DarkMatter
import com.github.kovah101.darkmatter.UNIT_SCALE
import com.github.kovah101.darkmatter.V_WIDTH
import com.github.kovah101.darkmatter.assets.I18NBundleAsset
import com.github.kovah101.darkmatter.assets.MusicAsset
import com.github.kovah101.darkmatter.ecs.components.*
import com.github.kovah101.darkmatter.ecs.system.DAMAGE_AREA_HEIGHT
import com.github.kovah101.darkmatter.ecs.system.addExplosion
import com.github.kovah101.darkmatter.ecs.system.createDarkMatter
import com.github.kovah101.darkmatter.ecs.system.createPlayer
import com.github.kovah101.darkmatter.event.GameEvent
import com.github.kovah101.darkmatter.event.GameEventListener
import com.github.kovah101.darkmatter.ui.GameUI
import ktx.actors.plusAssign
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
    private  val ui = GameUI(assets[I18NBundleAsset.DEFAULT.descriptor])


    override fun show() {
        super.show()
        LOG.debug { "Game screen shown" }
        LOG.debug { "High Score: ${preferences["highscore", 0]}" }
        gameEventManager.run {
            addListener(GameEvent.PlayerDeath::class, this@GameScreen)
            addListener(GameEvent.PlayerHit::class, this@GameScreen)
            addListener(GameEvent.PlayerMove::class, this@GameScreen)
            addListener(GameEvent.PlayerBlock::class, this@GameScreen)
            addListener(GameEvent.PlayerDeath::class, this@GameScreen)
        }

        engine.run {
            // attempt to stop constant player respawning & music restarting
            LOG.debug { "player alive = $playerAlive" }
            if (!playerAlive) {
                audioService.play(MusicAsset.GAME)
                createPlayer(assets)
                createDarkMatter()
                playerAlive = true
            }
        }
            setupUI()
    }

    private fun setupUI(){
        ui.run {
            updateDistance(0f)
            updateLife(MAX_LIFE, MAX_LIFE)
            updateShield(0f, MAX_SHIELD)

        }
        stage += ui
    }


    override fun hide() {
        super.hide()
        gameEventManager.removeListener(this)
    }


    override fun render(delta: Float) {
        val deltaTime = min(MAX_DELTA_TIME, delta)
        engine.update(deltaTime)
        audioService.update()
        stage.run {
            viewport.apply()
            act(deltaTime)
            draw()
        }
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
            is GameEvent.CollectPowerUp -> {
                onCollectPowerUp(event)
            }

            is GameEvent.PlayerHit -> {
                ui.run {
                    updateLife(event.life, event.maxLife)
                }
            }
            is GameEvent.PlayerBlock -> {
                ui.updateShield(event.shield, event.maxShield)
            }

            is GameEvent.PlayerMove -> {
                ui.updateDistance(event.distance)
                ui.updateSpeed(event.speed)
            }


        }

    }

    private fun onCollectPowerUp(event : GameEvent.CollectPowerUp){
        // check for player component in event
        event.player[PlayerComponent.mapper]?.let { player ->
            when(event.type){
                PowerUpType.LIFE -> ui.updateLife(player.life, player.maxLife)
                PowerUpType.SHIELD -> ui.updateShield(player.shield, player.maxShield)
                else -> {
                    // ignore &
                    //return
                }
            }

        }
    }

}


