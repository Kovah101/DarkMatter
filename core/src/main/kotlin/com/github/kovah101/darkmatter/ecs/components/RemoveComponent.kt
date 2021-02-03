package com.github.kovah101.darkmatter.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class RemoveComponent: Component, Pool.Poolable {
    // used to mark entities for removal
    // implement RemoveSystem to remove entities last
    var delay = 0f

    override fun reset() {
        delay = 0f
    }

    companion object {
        val mapper = mapperFor<RemoveComponent>()
    }
}