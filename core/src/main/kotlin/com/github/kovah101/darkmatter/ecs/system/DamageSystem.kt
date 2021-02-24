package com.github.kovah101.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Rectangle
import com.github.kovah101.darkmatter.ecs.components.*
import com.github.kovah101.darkmatter.event.GameEventManager
import com.github.kovah101.darkmatter.event.GameEvent
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import ktx.log.debug
import ktx.log.logger
import kotlin.math.max


private val LOG = logger<DamageSystem>()
const val DAMAGE_AREA_HEIGHT = 2f
private const val DAMAGE_PER_SECOND = 25f
private const val DEATH_EXPLOSION_DELAY = 0.9f // delay till death

class DamageSystem (
    private val gameEventManager: GameEventManager
        ) :
    IteratingSystem(allOf(PlayerComponent::class, TransformComponent::class).exclude(RemoveComponent::class).get()) {
    private val playerBoundRect = Rectangle()
    private val enemyBoundRect = Rectangle()

    // by lazy to initialise later
    private val enemyEntities by lazy {
        engine.getEntitiesFor(
            allOf(EnemyComponent::class).exclude(RemoveComponent::class).get()
        )
    }


    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "Entity |entity| must have a TransformComponent. entity=$entity" }
        val player = entity[PlayerComponent.mapper]
        require(player != null) { "Entity |entity| must have a PlayerComponent. entity=$entity" }

        // on death
        if (player.life <= 0f) {
            gameEventManager.dispatchEvent(GameEvent.PlayerDeath.apply {
                this.distance = player.distance
            })
            entity.addComponent<RemoveComponent>(engine) {
                delay = DEATH_EXPLOSION_DELAY
            }
            entity[GraphicComponent.mapper]?.sprite?.setAlpha(0f)
            engine.addExplosion(transform)
        }

        if (transform.position.y <= DAMAGE_AREA_HEIGHT) {
            var damage = DAMAGE_PER_SECOND * deltaTime

            // block damage with shield first
            if (player.shield > 0f) {
                val blockAmount = player.shield
                player.shield = max( player.shield - damage, 0f)
                gameEventManager.dispatchEvent(GameEvent.PlayerBlock.apply {
                    shield = player.shield
                    maxShield = player.maxShield
                })
                damage -= blockAmount

                if (damage <= 0f) {
                    // entire damage blocked by shield
                    return
                }
            }
            player.life -= damage
            gameEventManager.dispatchEvent(GameEvent.PlayerHit.apply {
                this.player = entity
                life = player.life
                maxLife = player.maxLife
            })

        }

        // process player on enemy collision damage
        // set hitboxes
         playerBoundRect.set(
             transform.position.x,
             transform.position.y,
             transform.size.x,
             transform.size.y
         )

        enemyEntities.forEach { enemy ->
            enemy[TransformComponent.mapper]?.let {
                enemyBoundRect.set(
                    it.position.x,
                    it.position.y,
                    it.size.x,
                    it.size.y
                )
            }

            if (playerBoundRect.overlaps(enemyBoundRect)){
                damagePlayer(entity, enemy)
            }
        }
    }

    private fun damagePlayer ( entity: Entity, enemy : Entity) {
        //damage player
        val player = entity[PlayerComponent.mapper]
        require(player != null) { "Entity |entity| must have a PlayerComponent. entity=$entity" }
        var damage = enemy[EnemyComponent.mapper]?.type?.damage
        require(damage != null) { "Enemy |enemy| must have a EnemyComponent. enemy=$enemy" }
        // block damage with shield first
        if (player.shield > 0f) {
            val blockAmount = player.shield
            player.shield = max( player.shield - damage, 0f)
            gameEventManager.dispatchEvent(GameEvent.PlayerBlock.apply {
                shield = player.shield
                maxShield = player.maxShield
            })
            damage -= blockAmount

            if (damage <= 0f) {
                // entire damage blocked by shield
                return
            }
        }
        player.life -= damage
        gameEventManager.dispatchEvent(GameEvent.PlayerHit.apply {
            this.player = entity
            life = player.life
            maxLife = player.maxLife
        })

        // destroy enemy
        val enemyTrans = enemy[TransformComponent.mapper]
        require(enemyTrans != null) { "Enemy |enemy| must have a TransformComponent. enemy=$enemy" }

        LOG.debug { "We have collision! Enemy destroyed" }
        enemy.addComponent<RemoveComponent>(engine) {
            delay = DEATH_EXPLOSION_DELAY
        }
        enemy[GraphicComponent.mapper]?.sprite?.setAlpha(0f)
        engine.addExplosion(enemyTrans)
    }
}