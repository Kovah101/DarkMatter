package com.github.kovah101.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
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
        with<PlayerComponent>()
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

fun Engine.createDarkMatter(){
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