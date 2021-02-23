package com.github.kovah101.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.github.kovah101.darkmatter.ecs.components.*
import com.github.kovah101.darkmatter.event.GameEventManager
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import ktx.log.debug
import ktx.log.logger

private val LOG = logger<ProjectileSystem>()

class ProjectileSystem(
    private val gameEventManager: GameEventManager
) : IteratingSystem(
    allOf(ProjectileComponent::class, TransformComponent::class).exclude(RemoveComponent::class).get()
) {

    // by lazy to initialise later
    private val enemyEntities by lazy {
        engine.getEntitiesFor(
            allOf(EnemyComponent::class).exclude(RemoveComponent::class).get()
        )
    }


    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "Entity |entity| must have a TransformComponent. entity=$entity" }
        val projectile = entity[ProjectileComponent.mapper]
        require(projectile != null) { "Entity |entity| must have a ProjectileComponent. entity=$entity" }

        // TODO move to enemy system with spawn patterns
        enemyEntities.forEach { enemy ->
            //LOG.debug { "There are ${enemyEntities.size()} enemies" }
            enemy[TransformComponent.mapper]?.let { enemyTrans ->
                if (enemyTrans.position.y <= 1f) {
                    enemy.addComponent<RemoveComponent>(engine)
                }
            }
        }

        if (transform.position.y >= 16f) {
            //projectile off the screen so remove
            entity.addComponent<RemoveComponent>(engine)
            return
        }
    }
}