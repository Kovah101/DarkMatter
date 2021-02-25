package com.github.kovah101.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.github.kovah101.darkmatter.V_WIDTH
import com.github.kovah101.darkmatter.ecs.components.*
import com.github.kovah101.darkmatter.event.GameEventManager
import ktx.ashley.*
import ktx.collections.GdxArray
import ktx.collections.gdxArrayOf
import ktx.log.debug
import ktx.log.logger
import java.rmi.activation.ActivationGroup.getSystem

private val LOG = logger<EnemySystem>()
private const val DEATH_EXPLOSION_DELAY = 0.9f // delay till death
private const val MAX_SPAWN_INTERVAL = 1.5f
private const val MIN_SPAWN_INTERVAL = 0.9f

private class EnemySpawnPattern(
    type1 : EnemyType = EnemyType.NONE,
    type2 : EnemyType = EnemyType.NONE,
    type3 : EnemyType = EnemyType.NONE,
    type4 : EnemyType = EnemyType.NONE,
    type5 : EnemyType = EnemyType.NONE,
    type6 : EnemyType = EnemyType.NONE,
    val types : GdxArray<EnemyType> = gdxArrayOf(type1, type2, type3, type4, type5, type6)
)

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
    // TODO Spawn system & patterns
    private var spawnTimer = 0f
    private val spawnPatterns = gdxArrayOf(
        EnemySpawnPattern(type1 = EnemyType.ASTEROID_CHUNK, type3= EnemyType.ASTEROID_CHUNK, type5 = EnemyType.ASTEROID_CHUNK),
        EnemySpawnPattern(type2 = EnemyType.ASTEROID_CHUNK, type3= EnemyType.ASTEROID_CHUNK, type4 = EnemyType.ASTEROID_CHUNK),
        EnemySpawnPattern(type1 = EnemyType.ASTEROID_CHUNK, type5= EnemyType.ASTEROID_CHUNK, type6 = EnemyType.ASTEROID_CHUNK)
    )

    private val currentSpawnPattern = GdxArray<EnemyType>()

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        spawnTimer -= deltaTime
        if (spawnTimer <= 0f) {
            spawnTimer = MathUtils.random(MIN_SPAWN_INTERVAL, MAX_SPAWN_INTERVAL)

            if (currentSpawnPattern.isEmpty) {
                currentSpawnPattern.addAll(spawnPatterns[MathUtils.random(0, spawnPatterns.size - 1)].types)
                LOG.debug { "Next pattern: $currentSpawnPattern" }
            }
            // select enemy type + remove from pattern
            val enemyType = currentSpawnPattern.removeIndex(0)
            if(enemyType == EnemyType.NONE){
                // do nothing
                return
            }
            spawnEnemy(enemyType, 1f * MathUtils.random(0, V_WIDTH-1), 16f)
        }
    }

    private fun spawnEnemy(enemyType: EnemyType, x : Float, y : Float){
        engine.entity{
            with<TransformComponent>{
                //size.set(1f,1f)
                setInitialPosition(x, y, 0f)
            }
            with<AnimationComponent>{type = enemyType.animationType}
            with<GraphicComponent>()
            with<EnemyComponent>{
                type = enemyType
                enemyType.health = enemyType.maxHealth
            }
            with<MoveComponent>{speed.y = enemyType.speed}

        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "Entity |entity| must have a TransformComponent. entity=$entity" }
        val enemy = entity[EnemyComponent.mapper]
        require(enemy != null) { "Entity |entity| must have a EnemyComponent. entity=$entity" }

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

        val damage = projectile[ProjectileComponent.mapper]?.type?.damage
        require(damage != null) { "Projectile |projectile| must have a ProjectileComponent. projectile=$projectile" }
        LOG.debug { "enemy health =${enemy.type.health}" }
        enemy.type.health -= damage
        LOG.debug { "enemy health =${enemy.type.health}" }
        if (enemy.type.health <= 0) {
            destroyEnemy(entity, transform)
            // spawn power up on destroying enemy
            engine.getSystem<PowerUpSystem>().spawnPowerUp(PowerUpType.SPEED_2, transform.position.x, transform.position.y)
        }
        // destroy projectile on successful hit
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