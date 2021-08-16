package com.github.kovah101.darkmatter.screen


import com.badlogic.ashley.core.Engine
import com.github.kovah101.darkmatter.DarkMatter
import com.github.kovah101.darkmatter.assets.GlobalDifficulty
import com.github.kovah101.darkmatter.assets.I18NBundleAsset
import com.github.kovah101.darkmatter.assets.MusicAsset
import com.github.kovah101.darkmatter.assets.SoundAsset
import com.github.kovah101.darkmatter.ecs.components.*
import com.github.kovah101.darkmatter.ecs.system.*
import com.github.kovah101.darkmatter.event.GameEvent
import com.github.kovah101.darkmatter.event.GameEventListener
import com.github.kovah101.darkmatter.ui.GameUI
import ktx.actors.plusAssign
import ktx.ashley.get
import ktx.ashley.getSystem
import ktx.log.debug
import ktx.log.logger
import ktx.preferences.flush
import ktx.preferences.get
import ktx.preferences.set
import kotlin.math.min
import kotlin.math.roundToInt

private val LOG = logger<GameScreen>()
private const val MAX_DELTA_TIME = 1 / 20f //used to stop spiral of death
private var playerAlive : Boolean = false
private var bonusScore = 0f
var currentDifficulty : GlobalDifficulty = GlobalDifficulty.EASY

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
            addListener(GameEvent.CollectPowerUp::class, this@GameScreen)
            addListener(GameEvent.PlayerShoot::class, this@GameScreen)
            addListener(GameEvent.EnemyDestroyed::class, this@GameScreen)
        }

        engine.run {
            // attempt to stop constant player respawning & music restarting
            LOG.debug { "player alive = $playerAlive" }
            if (!playerAlive) {
                audioService.play(MusicAsset.GAME)
                createPlayer(assets)
                createEventHorizon()
                playerAlive = true
                bonusScore = 0f
                currentDifficulty = GlobalDifficulty.EASY
                LOG.debug { "current difficulty=$currentDifficulty" }
                getSystem<PowerUpSystem>().setProcessing(true)
                getSystem<DamageSystem>().setProcessing(true)
                getSystem<EnemySystem>().setProcessing(true)
            }
        }
            setupUI()
    }

    private fun setupUI(){
        ui.run {
            updateScore(0f, bonusScore)
            updateLife(MAX_LIFE, MAX_LIFE)
            updateShield(10f, MAX_SHIELD)
            updateAmmo(MAX_AMMO.toFloat(), MAX_AMMO.toFloat())
        }
        stage += ui
    }


    override fun hide() {
        super.hide()
        gameEventManager.removeListener(this)
        engine.run {
            removeAllEntities()
            getSystem<PowerUpSystem>().setProcessing(false)
            getSystem<DamageSystem>().setProcessing(false)
            getSystem<EnemySystem>().setProcessing(false)

        }
        stage.clear()
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
                onPlayerDeath(event)
            }
            is GameEvent.CollectPowerUp -> {
                onCollectPowerUp(event)
            }

            is GameEvent.PlayerHit -> {
                audioService.play(SoundAsset.DAMAGE)
                ui.run {
                    updateLife(event.life, event.maxLife)
                }
            }
            is GameEvent.PlayerBlock -> {
                audioService.play(SoundAsset.BLOCK)
                ui.updateShield(event.shield, event.maxShield)
            }

            is GameEvent.PlayerMove -> {
                ui.updateScore(event.distance, bonusScore)
            }
            is GameEvent.PlayerShoot -> {
                // play laser sound TODO test sounds
                audioService.play(SoundAsset.LASER_1)
                ui.updateAmmo(event.ammo.toFloat(), event.maxAmmo.toFloat())
            }
            is GameEvent.EnemyDestroyed -> {
                bonusScore += event.bonusPoints
            }

        }

    }

    private fun onPlayerDeath(event: GameEvent.PlayerDeath) {
        val totalScore = ((event.distance * 10) + bonusScore).roundToInt()
        LOG.debug { "Player died with a distance of $totalScore " }
        // store high score
        if (totalScore > preferences["highscore", 0]) {
            preferences.flush {
                this["highscore"] = totalScore
            }
        }
        game.getScreen<GameOverScreen>().run {
            score = totalScore
            highScore = preferences["highscore", 0]
        }
        game.setScreen<GameOverScreen>()
    }

    private fun onCollectPowerUp(event : GameEvent.CollectPowerUp){
        // check for player component in event
        event.player[PlayerComponent.mapper]?.let { player ->
            when(event.type){
                PowerUpType.LIFE -> {
                    ui.updateLife(player.life, player.maxLife)
                }
                PowerUpType.SHIELD -> {
                    ui.updateShield(player.shield, player.maxShield)
                }
                PowerUpType.AMMO -> {
                    ui.updateAmmo(player.ammo.toFloat(), player.maxAmmo.toFloat())
                }
                else -> {
                    // ignore &
                    //return
                }
            }
        }
    }

}


