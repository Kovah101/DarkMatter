package com.github.kovah101.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.github.kovah101.darkmatter.V_WIDTH
import com.github.kovah101.darkmatter.audio.AudioService
import com.github.kovah101.darkmatter.ecs.components.*
import com.github.kovah101.darkmatter.event.GameEvent
import com.github.kovah101.darkmatter.event.GameEventManager
import com.github.kovah101.darkmatter.screen.currentDifficulty
import ktx.ashley.*
import ktx.collections.GdxArray
import ktx.collections.gdxArrayOf
import ktx.log.debug
import ktx.log.logger
import kotlin.math.min


private val LOG = logger<PowerUpSystem>()
private const val MAX_SPAWN_INTERVAL = 1.4f
private const val MIN_SPAWN_INTERVAL = 0.8f
private const val POWER_UP_SPEED = -8.75f //fall speed

// TODO adjust power up fall speed for more fluid gameplay

private class SpawnPattern(
    type1: PowerUpType = PowerUpType.NONE,
    type2: PowerUpType = PowerUpType.NONE,
    type3: PowerUpType = PowerUpType.NONE,
    type4: PowerUpType = PowerUpType.NONE,
    type5: PowerUpType = PowerUpType.NONE,
    type6: PowerUpType = PowerUpType.NONE,
    val types: GdxArray<PowerUpType> = gdxArrayOf(type1, type2, type3, type4, type5, type6)
)

class PowerUpSystem(
    private val gameEventManager: GameEventManager,
    private val audioService: AudioService
) :
    IteratingSystem(allOf(PowerUpComponent::class, TransformComponent::class).exclude(RemoveComponent::class).get()) {
    private val playerBoundingRect = Rectangle()
    private val powerUpBoundRect = Rectangle()

    // by lazy to initialise later
    private val playerEntities by lazy {
        engine.getEntitiesFor(
            allOf(PlayerComponent::class).exclude(RemoveComponent::class).get()
        )
    }
    private var spawnTime = 0f
    private val spawnPatterns = gdxArrayOf(
        SpawnPattern(
            type1 = PowerUpType.SPEED_1,
            type2 = PowerUpType.LIFE,
            type4 = PowerUpType.AMMO,
            type6 = PowerUpType.SPEED_2
        ),
        SpawnPattern(
            type2 = PowerUpType.LIFE,
            type3 = PowerUpType.SHIELD,
            type4 = PowerUpType.SPEED_1,
            type6 = PowerUpType.AMMO
        ),
        SpawnPattern(
            type1 = PowerUpType.SPEED_1,
            type3 = PowerUpType.LIFE,
            type5 = PowerUpType.SHIELD,
            type6 = PowerUpType.SPEED_1
        )
    )
    private val currentSpawnPattern = GdxArray<PowerUpType>()

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        spawnTime -= deltaTime
        if (spawnTime <= 0f) {
            spawnTime = MathUtils.random(MIN_SPAWN_INTERVAL, MAX_SPAWN_INTERVAL)

            if (currentSpawnPattern.isEmpty) {
                currentSpawnPattern.addAll(spawnPatterns[MathUtils.random(0, spawnPatterns.size - 1)].types)
                LOG.debug { "Difficulty=$currentDifficulty" }
                LOG.debug { "Next pattern: $currentSpawnPattern" }
            }

            // select power up + remove it from pattern
            val powerUpType = currentSpawnPattern.removeIndex(0)
            if (powerUpType == PowerUpType.NONE) {
                // nothing to spawn
                return
            }
            spawnPowerUp(powerUpType, 1f * MathUtils.random(0, V_WIDTH - 1), 16f, POWER_UP_SPEED)
        }
    }

    fun spawnPowerUp(powerUpType: PowerUpType, x: Float, y: Float, fallSpeed: Float) {
        engine.entity {
            with<TransformComponent> {
                setInitialPosition(x, y, 0f)
            }
            with<PowerUpComponent> { type = powerUpType }
            with<AnimationComponent> { type = powerUpType.animationType }
            with<GraphicComponent>()
            with<MoveComponent> { speed.y = fallSpeed }
        }
    }


    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "Entity |entity| must have a TransformComponent. entity=$entity" }

        if (transform.position.y <= 1f) {
            //power up not collected so removed
            entity.addComponent<RemoveComponent>(engine)
            return
        }

        powerUpBoundRect.set(
            transform.position.x,
            transform.position.y,
            transform.size.x,
            transform.size.y
        )

        playerEntities.forEach { player ->
            player[TransformComponent.mapper]?.let { playerTransform ->
                playerBoundingRect.set(
                    playerTransform.position.x,
                    playerTransform.position.y,
                    playerTransform.size.x,
                    playerTransform.size.y
                )

                if (playerBoundingRect.overlaps(powerUpBoundRect)) {
                    collectPowerUp(player, entity)
                }
            }
        }
    }

    private fun collectPowerUp(player: Entity, powerUp: Entity) {
        val powerUpCmp = powerUp[PowerUpComponent.mapper]
        require(powerUpCmp != null) { "Entity |entiy| must have a PowerUpComponent. entity=$powerUp" }

        powerUpCmp.type.also { powerUpType ->
            LOG.debug { "Picking up power up of type:${powerUpCmp.type}" }

            player[MoveComponent.mapper]?.let { it.speed.y += powerUpType.speedGain }
            player[PlayerComponent.mapper]?.let {
                it.life = min(it.maxLife, it.life + powerUpType.lifeGain)
                it.shield = min(it.maxShield, it.shield + powerUpType.shieldGain)
                it.ammo = min(it.maxAmmo, it.ammo + powerUpType.ammoGain)
            }
            audioService.play(powerUpType.soundAsset)

            gameEventManager.dispatchEvent(
                GameEvent.CollectPowerUp.apply {
                    this.player = player
                    type = powerUpType
                    LOG.debug { "Power Up event sent!" }
                })
        }
        powerUp.addComponent<RemoveComponent>(engine)
    }
}