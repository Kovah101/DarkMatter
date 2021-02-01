package com.github.kovah101.darkmatter.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class GraphicComponent: Component, Pool.Poolable  {
    val sprite = Sprite()

    //clear sprite and reset colour
    override fun reset() {
        sprite.texture = null
        sprite.setColor(1f,1f,1f,1f)
    }

    companion object{
        val mapper = mapperFor<GraphicComponent>()
    }
}