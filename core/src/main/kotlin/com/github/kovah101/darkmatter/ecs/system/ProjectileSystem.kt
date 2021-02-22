package com.github.kovah101.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.github.kovah101.darkmatter.ecs.components.PlayerComponent
import com.github.kovah101.darkmatter.ecs.components.ProjectileComponent
import com.github.kovah101.darkmatter.ecs.components.RemoveComponent
import com.github.kovah101.darkmatter.ecs.components.TransformComponent
import com.github.kovah101.darkmatter.event.GameEventManager
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get

class ProjectileSystem(
    private val gameEventManager: GameEventManager
) : IteratingSystem(allOf(ProjectileComponent::class, TransformComponent::class).exclude(RemoveComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "Entity |entity| must have a TransformComponent. entity=$entity" }
        val projectile = entity[ProjectileComponent.mapper]
        require(projectile != null) { "Entity |entity| must have a ProjectileComponent. entity=$entity" }


        if (transform.position.y >= 7f) {
            //power up not collected so removed
            entity.addComponent<RemoveComponent>(engine)
            return
        }
    }
}