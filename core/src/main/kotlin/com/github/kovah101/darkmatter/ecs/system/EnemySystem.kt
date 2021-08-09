package com.github.kovah101.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Event
import com.github.kovah101.darkmatter.V_WIDTH
import com.github.kovah101.darkmatter.assets.GlobalDifficulty
import com.github.kovah101.darkmatter.audio.AudioService
import com.github.kovah101.darkmatter.ecs.components.*
import com.github.kovah101.darkmatter.event.GameEvent
import com.github.kovah101.darkmatter.event.GameEventListener
import com.github.kovah101.darkmatter.event.GameEventManager
import com.github.kovah101.darkmatter.screen.currentDifficulty
import ktx.ashley.*
import ktx.collections.GdxArray
import ktx.collections.gdxArrayOf
import ktx.log.debug
import ktx.log.logger
import java.rmi.activation.ActivationGroup.getSystem

private val LOG = logger<EnemySystem>()
private const val DEATH_EXPLOSION_DELAY = 0.9f // delay till death
private const val EASY_MAX_SPAWN_INTERVAL = 1.5f
private const val EASY_MIN_SPAWN_INTERVAL = 0.9f
private const val MED_MAX_SPAWN_INTERVAL = 1.0f
private const val MED_MIN_SPAWN_INTERVAL = 0.65f
private const val HARD_MAX_SPAWN_INTERVAL = 0.7f
private const val HARD_MIN_SPAWN_INTERVAL = 0.35f

private class EnemySpawnPattern(
    type1: EnemyType = EnemyType.NONE,
    type2: EnemyType = EnemyType.NONE,
    type3: EnemyType = EnemyType.NONE,
    type4: EnemyType = EnemyType.NONE,
    type5: EnemyType = EnemyType.NONE,
    val types: GdxArray<EnemyType> = gdxArrayOf(type1, type2, type3, type4, type5)
)

class EnemySystem(
    private val gameEventManager: GameEventManager,
    private val audioService: AudioService
) : GameEventListener, IteratingSystem(
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

    private var spawnTimer = 0f
    private var minSpawnTimer = currentDifficulty.minEnemySpawnTimer
    private var maxSpawnTimer = currentDifficulty.maxEnemySpawnTimer
    private var speedMultiplier = currentDifficulty.pullSpeedMultiplier
    private val spawnPatterns = gdxArrayOf(
        EnemySpawnPattern(
            type1 = EnemyType.ASTEROID_CHUNK,
            type3 = EnemyType.ASTEROID_CHIP,
            type5 = EnemyType.ASTEROID_SMALL
        ),
        EnemySpawnPattern(
            type2 = EnemyType.ASTEROID_EGG,
            type3 = EnemyType.ASTEROID_LONG,
            type4 = EnemyType.ASTEROID_CHUNK
        ),
        EnemySpawnPattern(
            type1 = EnemyType.ASTEROID_EGG,
            type3 = EnemyType.ASTEROID_CHIP,
            type5 = EnemyType.ASTEROID_LONG
        )
    )

    private val currentSpawnPattern = GdxArray<EnemyType>()

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        gameEventManager.addListener(GameEvent.PlayerMove::class, this)
    }

    // TODO Add removedFromEngine like Render System, adjust spawn patterns for more variety and re-look at Asteroid stats

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        spawnTimer -= deltaTime
        if (spawnTimer <= 0f) {
            spawnTimer = MathUtils.random(minSpawnTimer, maxSpawnTimer)

            if (currentSpawnPattern.isEmpty) {
                currentSpawnPattern.addAll(spawnPatterns[MathUtils.random(0, spawnPatterns.size - 1)].types)
                LOG.debug { "Next pattern: $currentSpawnPattern" }
            }
            // select enemy type + remove from pattern
            val enemyType = currentSpawnPattern.removeIndex(0)
            if (enemyType == EnemyType.NONE) {
                // do nothing
                return
            }
            spawnEnemy(enemyType, 1f * MathUtils.random(0, V_WIDTH - 1), 16f)
        }
    }

    private fun spawnEnemy(enemyType: EnemyType, x: Float, y: Float) {
        engine.entity {
            with<TransformComponent> {
                //size.set(1f,1f)
                setInitialPosition(x, y, 0f)
            }
            with<AnimationComponent> { type = enemyType.animationType }
            with<GraphicComponent>()
            with<EnemyComponent> {
                type = enemyType
                enemyType.health = enemyType.maxHealth
            }
            with<MoveComponent> { speed.y = enemyType.speed * speedMultiplier }

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

    // collision between laser and enemies
    private fun damageEnemy(entity: Entity, projectile: Entity) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "Entity |entity| must have a TransformComponent. entity=$entity" }
        val enemy = entity[EnemyComponent.mapper]
        require(enemy != null) { "Entity |entity| must have a EnemyComponent. entity=$entity" }

        val damage = projectile[ProjectileComponent.mapper]?.type?.damage
        require(damage != null) { "Projectile |projectile| must have a ProjectileComponent. projectile=$projectile" }
        //LOG.debug { "enemy health =${enemy.type.health}" }
        enemy.type.health -= damage
        //LOG.debug { "enemy health =${enemy.type.health}" }
        if (enemy.type.health <= 0) {
            destroyEnemy(entity, transform)
            // dispatch event to add bonus points
            gameEventManager.dispatchEvent(
                GameEvent.EnemyDestroyed.apply {
                    bonusPoints = enemy.type.damage
                    LOG.debug { "enemy destroyed event sent!" }
                }
            )
            // spawn power up on destroying enemy
            if (enemy.type == EnemyType.ASTEROID_EGG) {
                engine.getSystem<PowerUpSystem>()
                    .spawnPowerUp(PowerUpType.SPEED_2, transform.position.x, transform.position.y, -4f)
            }else if (enemy.type == EnemyType.ASTEROID_CHUNK){
                engine.getSystem<PowerUpSystem>()
                    .spawnPowerUp(PowerUpType.SPEED_1, transform.position.x, transform.position.y, -4f)
            }

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
        //play enemy death noise
        audioService.play(entity.getComponent(EnemyComponent::class.java).type.soundAsset)
    }

    // TODO Adjust difficulty to make easier at the start and more granular
    // log changes to make sure its working
    override fun onEvent(event: GameEvent) {
        if (event is GameEvent.PlayerMove) {
            when {
                event.distance.toInt() == 15 -> {
                    // enter medium difficulty
                    currentDifficulty = GlobalDifficulty.MEDIUM
                    setDifficulty()
                }
                event.distance.toInt() == 28 -> {
                    // enter hard difficulty
                    currentDifficulty = GlobalDifficulty.HARD
                    setDifficulty()
                }
                event.distance.toInt() == 36 -> {
                    // enter hard difficulty
                    currentDifficulty = GlobalDifficulty.EXTRA_HARD
                    setDifficulty()
                }
            }
        }

    }

    private fun setDifficulty() {
        minSpawnTimer = currentDifficulty.minEnemySpawnTimer
        maxSpawnTimer = currentDifficulty.maxEnemySpawnTimer
        speedMultiplier = currentDifficulty.pullSpeedMultiplier
        //LOG.debug { "DIFFICULTY=$currentDifficulty, minSpawnTimer=$minSpawnTimer, maxSpawnTimer=$maxSpawnTimer" }
    }
}