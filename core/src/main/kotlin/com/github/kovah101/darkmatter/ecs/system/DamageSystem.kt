package com.github.kovah101.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.github.kovah101.darkmatter.ecs.components.GraphicComponent
import com.github.kovah101.darkmatter.ecs.components.PlayerComponent
import com.github.kovah101.darkmatter.ecs.components.RemoveComponent
import com.github.kovah101.darkmatter.ecs.components.TransformComponent
import com.github.kovah101.darkmatter.event.GameEventManager
import com.github.kovah101.darkmatter.event.GameEvent
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import kotlin.math.max

const val DAMAGE_AREA_HEIGHT = 2f
private const val DAMAGE_PER_SECOND = 25f
private const val DEATH_EXPLOSION_DELAY = 0.9f // delay till death

class DamageSystem (
    private val gameEventManager: GameEventManager
        ) :
    IteratingSystem(allOf(PlayerComponent::class, TransformComponent::class).exclude(RemoveComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "Entity |entity| must have a TransformComponent. entity=$entity" }
        val player = entity[PlayerComponent.mapper]
        require(player != null) { "Entity |entity| must have a PlayerComponent. entity=$entity" }


        if (transform.position.y <= DAMAGE_AREA_HEIGHT) {
            var damage = DAMAGE_PER_SECOND * deltaTime
            // block damage with shield first
            if (player.shield > 0f) {
                val blockAmount = player.shield
                player.shield = max(0f, player.shield - damage)
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
        }
    }
}