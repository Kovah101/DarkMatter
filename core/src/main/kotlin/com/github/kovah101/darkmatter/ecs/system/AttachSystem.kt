package com.github.kovah101.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import com.github.kovah101.darkmatter.ecs.components.AttachComponent
import com.github.kovah101.darkmatter.ecs.components.GraphicComponent
import com.github.kovah101.darkmatter.ecs.components.RemoveComponent
import com.github.kovah101.darkmatter.ecs.components.TransformComponent
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.get

class AttachSystem :
    EntityListener,
    IteratingSystem(allOf(AttachComponent::class, TransformComponent::class, GraphicComponent::class).get()) {
    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
    }

    override fun entityAdded(entity: Entity) = Unit

    override fun entityRemoved(removedEntity: Entity) {
        // for all attached entities add remove component so they are removed
        entities.forEach { entity ->
            entity[AttachComponent.mapper]?.let { attach ->
                if (attach.entity == removedEntity) {
                    entity.addComponent<RemoveComponent>(engine)
                }
            }
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val attach = entity[AttachComponent.mapper]
        require(attach != null) {"Entity |entity| must have an AttachComponent. entity=$entity"}
        val graphic = entity[GraphicComponent.mapper]
        require(graphic != null) {"Entity |entity| must have an GraphicsComponent. entity=$entity"}
        val transform = entity[TransformComponent.mapper]
        require(transform != null) {"Entity |entity| must have an TransformComponent. entity=$entity"}

        // update position
        attach.entity[TransformComponent.mapper]?.let { attachTransform ->
            transform.interpolatedPosition.set(
                attachTransform.interpolatedPosition.x + attach.offset.x,
                attachTransform.interpolatedPosition.y + attach.offset.y,
                transform.position.z
            )
        }

        // update graphic alpha value
        attach.entity[GraphicComponent.mapper]?.let{ attachGraphic ->
            graphic.sprite.setAlpha(attachGraphic.sprite.color.a)
        }

    }


}