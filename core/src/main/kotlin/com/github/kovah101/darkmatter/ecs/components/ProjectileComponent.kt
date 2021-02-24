package com.github.kovah101.darkmatter.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

enum class ProjectileType (
    val atlasKey : String,
    val damage : Float,
    val speed : Float
) {
    NONE("", 0f, 0f),
    LASER("laser", 10f, 10f)
}

class ProjectileComponent: Component, Pool.Poolable {
    var type = ProjectileType.NONE

    override fun reset() {
       type = ProjectileType.NONE
    }

    companion object {
        val mapper = mapperFor<ProjectileComponent>()
    }
}