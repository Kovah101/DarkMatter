package com.github.kovah101.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.github.kovah101.darkmatter.UNIT_SCALE
import com.github.kovah101.darkmatter.V_HEIGHT
import com.github.kovah101.darkmatter.V_WIDTH
import com.github.kovah101.darkmatter.assets.TextureAtlasAsset
import com.github.kovah101.darkmatter.ecs.components.*
import ktx.ashley.entity
import ktx.ashley.with
import ktx.assets.async.AssetStorage


fun Engine.createPlayer (
    assets: AssetStorage,
    spawnX: Float = V_WIDTH * 0.5f,
    spawnY: Float = V_HEIGHT * 0.5f
): Entity{
    // player ship
    val playerShip = entity {
        with<TransformComponent> {
            val atlas = assets[TextureAtlasAsset.GAME_GRAPHICS.descriptor]
            val playerGraphicRegion = atlas.findRegion("ship_base")
            setInitialPosition(
                spawnX - size.x * 0.5f,
                spawnY - size.y * 0.5f,
                -1f)
        }
        with<MoveComponent>()
        with<GraphicComponent>()
        with<PlayerComponent>{
            ammo = MAX_AMMO
        }
        with<FacingComponent>()
    }

    // fire effect
    entity {
        with<TransformComponent>()
        with<AttachComponent> {
            entity = playerShip
            offset.set(0.8f * UNIT_SCALE, -6f * UNIT_SCALE)
        }
        with<GraphicComponent>()
        with<AnimationComponent> { type = AnimationType.FIRE }
    }
    return playerShip
}

fun Engine.createEventHorizon(){
    entity {
        with<TransformComponent> {
            size.set(
                V_WIDTH.toFloat(),
                DAMAGE_AREA_HEIGHT
            )
        }
        with<AnimationComponent> { type = AnimationType.DARK_MATTER }
        with<GraphicComponent>()
    }
}

fun Engine.addExplosion(transform : TransformComponent){
    entity {
        with<TransformComponent>{
            size.set(1.5f,1.5f)
            setInitialPosition(transform.position.x, transform.position.y, 2f)
        }
        with<GraphicComponent>()
        with<AnimationComponent>{ type = AnimationType.EXPLOSION}
        with<RemoveComponent>{
            delay = 1f
        }

    }
}

fun Engine.spawnLaser(transform: TransformComponent){
    entity {
        with<TransformComponent>{
            size.set(0.5f,0.75f)
            setInitialPosition(transform.position.x + (transform.size.x * 0.25f), transform.position.y + (transform.size.y * 0.5f), 2f)
        }
        with<AnimationComponent>{type = AnimationType.LASER}
        with<GraphicComponent>()
        with<ProjectileComponent>{type = ProjectileType.LASER}
        with<MoveComponent>{speed.y = ProjectileType.LASER.speed}
    }
}

fun Engine.spawnAsteroid(transform: TransformComponent){
    entity {
        with<TransformComponent>{
            size.set(1f,1f)
            setInitialPosition(5f, 14f, 2f)
        }
        with<AnimationComponent>{type = AnimationType.ASTEROID_CHUNK}
        with<GraphicComponent>()
        with<EnemyComponent>{
            type = EnemyType.ASTEROID_CHUNK
            EnemyType.ASTEROID_CHUNK.health = EnemyType.ASTEROID_CHUNK.maxHealth
        }
        with<MoveComponent>{speed.y = EnemyType.ASTEROID_CHUNK.speed}
    }
}