package com.github.kovah101.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.github.kovah101.darkmatter.ecs.components.*
import com.github.kovah101.darkmatter.event.GameEventManager
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import ktx.log.logger

private val LOG = logger<EnemySystem>()

class EnemySystem(
    private val gameEventManager: GameEventManager
) : IteratingSystem(
    allOf(EnemyComponent::class, TransformComponent::class).exclude(RemoveComponent::class).get()
) {




    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "Entity |entity| must have a TransformComponent. entity=$entity" }
        val enemy = entity[EnemyComponent.mapper]
        require(enemy != null) { "Entity |entity| must have a EnemyComponent. entity=$entity" }

        // TODO move to enemy system with spawn patterns


        if (transform.position.y <= 1f) {
            //projectile off the screen so remove
            entity.addComponent<RemoveComponent>(engine)
            return
        }
    }
}