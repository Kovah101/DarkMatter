package com.github.kovah101.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Rectangle
import com.github.kovah101.darkmatter.ecs.components.*
import com.github.kovah101.darkmatter.event.GameEventManager
import ktx.ashley.*
import ktx.log.debug
import ktx.log.logger
import java.rmi.activation.ActivationGroup.getSystem

private val LOG = logger<EnemySystem>()
private const val DEATH_EXPLOSION_DELAY = 0.9f // delay till death

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

        // TODO spawn patterns
        if (transform.position.y <= 1f) {
            //projectile off the screen so remove
            destroyEnemy(entity, transform)
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

    private fun damageEnemy(entity : Entity, projectile : Entity){
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "Entity |entity| must have a TransformComponent. entity=$entity" }
        val enemy = entity[EnemyComponent.mapper]
        require(enemy != null) { "Entity |entity| must have a EnemyComponent. entity=$entity" }

        // TODO damage and destroy both projectile and enemy
        val damage = projectile[ProjectileComponent.mapper]?.type?.damage
        require(damage != null) { "Projectile |projectile| must have a ProjectileComponent. projectile=$projectile" }

        enemy.type.health -= damage
        if (enemy.type.health < 0) {
            destroyEnemy(entity, transform)
            // spawn power up on destroying enemy
            engine.getSystem<PowerUpSystem>().spawnPowerUp(PowerUpType.SPEED_2, transform.position.x, transform.position.y)
        }

        projectile.addComponent<RemoveComponent>(engine)
    }

    private fun destroyEnemy(
        entity: Entity,
        transform: TransformComponent
    ) {
        entity.addComponent<RemoveComponent>(engine) {
            delay = DEATH_EXPLOSION_DELAY
        }
        entity[GraphicComponent.mapper]?.sprite?.setAlpha(0f)
        engine.addExplosion(transform)
    }
}