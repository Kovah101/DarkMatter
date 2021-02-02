package com.github.kovah101.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.kovah101.darkmatter.ecs.components.FacingComponent
import com.github.kovah101.darkmatter.ecs.components.FacingDirection
import com.github.kovah101.darkmatter.ecs.components.PlayerComponent
import com.github.kovah101.darkmatter.ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get

private const val TOUCH_TOLERANCE_DISTANCE = 0.2f

class PlayerInputSystem (
    private val gameViewport: Viewport,
        ): IteratingSystem(allOf(PlayerComponent::class, TransformComponent::class, FacingComponent::class).get()) {
    private val tmpVec = Vector2()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val facing = entity[FacingComponent.mapper]
        require(facing !=null) {"Entity |entity| must have FacingComponent. entity=$entity"}
        val transform = entity[TransformComponent.mapper]
        require(transform != null) {"Entity |entity| must have TransformComponent. entity=$entity"}

        //Takes mouse/touch input x coordinate and converts to world coordinate
        tmpVec.x = Gdx.input.x.toFloat()
        gameViewport.unproject(tmpVec)
        // finds difference in user input and player location
        val diffX = tmpVec.x - transform.position.x - transform.size.x * 0.5f
        // change facing depending on difference between position and input
        facing.direction = when {
            diffX < -TOUCH_TOLERANCE_DISTANCE -> FacingDirection.LEFT
            diffX > TOUCH_TOLERANCE_DISTANCE -> FacingDirection.RIGHT
            else -> FacingDirection.DEFAULT
        }
    }

        }