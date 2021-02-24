package com.github.kovah101.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Rectangle
import com.github.kovah101.darkmatter.ecs.components.*
import com.github.kovah101.darkmatter.event.GameEventManager
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import ktx.log.debug
import ktx.log.logger

private val LOG = logger<EnemySystem>()

class EnemySystem(
    private val gameEventManager: GameEventManager
) : IteratingSystem(
    allOf(EnemyComponent::class, TransformComponent::class).exclude(RemoveComponent::class).get()
) {
    private val projectileBoundRect = Rectangle()
    private val enemyBoundRect = Rectangle()

    // by lazy to initialise later
    private val projectileEntities by lazy {
        engine.getEntitiesFor(
            allOf(ProjectileComponent::class).exclude(RemoveComponent::class).get()
        )
    }


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

        //create bounds for enemies and projectiles
        enemyBoundRect.set(
            transform.position.x,
            transform.position.y,
            transform.size.x,
            transform.size.y
        )

        projectileEntities.forEach { projectile ->
            projectile[TransformComponent.mapper]?.let { projTrans ->
                projectileBoundRect.set(
                    projTrans.position.x,
                    projTrans.position.y,
                    projTrans.size.x,
                    projTrans.size.y
                )

                if (projectileBoundRect.overlaps(enemyBoundRect)) {
                    damageEnemy(entity, projectile)
                }

            }

        }

    }

    private fun damageEnemy(enemy : Entity, projectile : Entity){
        LOG.debug { "Enemy hit!" }
        // TODO damage and destroy both projectile and enemy
    }
}