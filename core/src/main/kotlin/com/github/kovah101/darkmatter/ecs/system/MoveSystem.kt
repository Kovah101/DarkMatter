package com.github.kovah101.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.github.kovah101.darkmatter.V_HEIGHT
import com.github.kovah101.darkmatter.V_WIDTH
import com.github.kovah101.darkmatter.ecs.components.*
import com.github.kovah101.darkmatter.event.GameEvent
import com.github.kovah101.darkmatter.event.GameEventManager
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.max

private const val UPDATE_RATE = 1 / 25f
// supersedes deltaTime in case of lag
// gives constant movement
private const val HOR_ACC = 18.5f
private const val VER_ACC = 2.25f
private const val MAX_VER_NEG_PLAYER_SPEED = 0.75f
private const val MAX_VER_POS_PLAYER_SPEED = 5f
private const val MAX_HOR_SPEED = 5.75f

class MoveSystem(
    private val gameEventManager: GameEventManager
) :
    IteratingSystem(allOf(TransformComponent::class, MoveComponent::class).exclude(RemoveComponent::class).get()) {
    private var accumulator = 0f

    override fun update(deltaTime: Float) {
        accumulator += deltaTime
        while (accumulator >= UPDATE_RATE) {
            accumulator -= UPDATE_RATE
            // save current position before updating to new one
            //allows for smooth movement
            entities.forEach {
                it[TransformComponent.mapper]?.let { transform ->
                    transform.prevPosition.set(transform.position)
                }
            }

            super.update(UPDATE_RATE)
        }
        
        val alpha = accumulator / UPDATE_RATE // % between frames
        entities.forEach { entity ->  
            entity[TransformComponent.mapper]?.let { transform ->
                transform.interpolatedPosition.set(
                    MathUtils.lerp(transform.prevPosition.x, transform.position.x, alpha),
                    MathUtils.lerp(transform.prevPosition.y, transform.position.y, alpha),
                    transform.position.z
                )
            }
        }
    }


    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "Entity |entity| must have a TransformComponent. entity=$entity" }
        val move = entity[MoveComponent.mapper]
        require(move != null) { "Entity |entity| must have a MoveComponent. entity=$entity" }

        val player = entity[PlayerComponent.mapper]
        if (player != null) {
            // player movement
            entity[FacingComponent.mapper]?.let { facing ->
                movePlayer(transform, move, player, facing, deltaTime)
            }
        } else {
            // other movement like power ups or enemies
            moveEntity(transform, move, deltaTime)
        }
    }

    private fun moveEntity(transform: TransformComponent, move: MoveComponent, deltaTime: Float) {
        transform.position.x = MathUtils.clamp(
            transform.position.x + move.speed.x * deltaTime,
            0f,
            V_WIDTH - transform.size.x
        )
        transform.position.y = MathUtils.clamp(
            transform.position.y + move.speed.y * deltaTime,
            1f,
            V_HEIGHT + 1f - transform.size.y // +1 so they start outside
        )
    }

    private fun movePlayer(
        transform: TransformComponent,
        move: MoveComponent,
        player: PlayerComponent,
        facing: FacingComponent,
        deltaTime: Float
    ) {
        // update horizontal speed
        // slowly accelerate to each side
        move.speed.x = when (facing.direction) {
            FacingDirection.LEFT -> min(0f, move.speed.x - HOR_ACC * deltaTime)
            FacingDirection.RIGHT -> max(0f, move.speed.x + HOR_ACC * deltaTime)
            else -> 0f
        }// cap speed
        move.speed.x = MathUtils.clamp(move.speed.x, -MAX_HOR_SPEED, MAX_HOR_SPEED)

        // update vertical speed
        // sucked into dark matter
        // constant neg from DM, const pos from boosts
        move.speed.y = MathUtils.clamp(
            move.speed.y - VER_ACC * deltaTime,
            -MAX_VER_NEG_PLAYER_SPEED,
            MAX_VER_POS_PLAYER_SPEED
        )
        // store original position
        val oldY = transform.position.y
        // move player
        moveEntity(transform, move, deltaTime)
        // update distance
        player.distance += abs(transform.position.y - oldY)
        // alert Game + UI of distance
        gameEventManager.dispatchEvent(GameEvent.PlayerMove.apply {
            distance = player.distance
            speed = move.speed.y
        })
    }
}