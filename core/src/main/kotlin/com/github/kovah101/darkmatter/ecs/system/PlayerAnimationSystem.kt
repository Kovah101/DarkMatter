package com.github.kovah101.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.github.kovah101.darkmatter.ecs.components.FacingComponent
import com.github.kovah101.darkmatter.ecs.components.FacingDirection
import com.github.kovah101.darkmatter.ecs.components.GraphicComponent
import com.github.kovah101.darkmatter.ecs.components.PlayerComponent
import ktx.ashley.allOf
import ktx.ashley.get

class PlayerAnimationSystem(
    private val defaultRegion: TextureRegion,
    private val leftRegion: TextureRegion,
    private val rightRegion: TextureRegion
) : IteratingSystem(allOf(PlayerComponent::class, FacingComponent::class, GraphicComponent::class).get()),
    EntityListener {

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(family, this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
    }

    override fun entityAdded(entity: Entity) {
        entity[GraphicComponent.mapper]?.setSpriteRegion(defaultRegion)
    }

    override fun entityRemoved(entity: Entity?) = Unit


    override fun processEntity(entity: Entity, deltaTime: Float) {
        val facing = entity[FacingComponent.mapper]
        require(facing !=null) {"Entity |entity| must have FacingComponent. entity=$entity"}
        val graphic = entity[GraphicComponent.mapper]
        require(graphic != null) {"Entity |entity| must have GraphicComponent. entity=$entity"}

        if(facing.direction == facing.lastDirection && graphic.sprite.texture != null) {
            // texture already set and direction has not changed -> do nothing
            return
            }
        //set last direction to current then set region to correct one due to facing
        facing.lastDirection = facing.direction
        val region = when(facing.direction){
            FacingDirection.LEFT -> leftRegion
            FacingDirection.RIGHT -> rightRegion
            else -> defaultRegion
        }
        graphic.setSpriteRegion(region)

    }
}