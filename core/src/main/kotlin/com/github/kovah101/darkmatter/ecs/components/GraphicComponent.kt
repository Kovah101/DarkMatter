package com.github.kovah101.darkmatter.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool
import com.github.kovah101.darkmatter.UNIT_SCALE
import ktx.ashley.mapperFor

class GraphicComponent: Component, Pool.Poolable  {
    val sprite = Sprite()

    //clear sprite and reset colour
    override fun reset() {
        sprite.texture = null
        sprite.setColor(1f,1f,1f,1f)
    }

    //helper function to set sprite texture, size, rotation
    fun setSpriteRegion(region: TextureRegion){
        sprite.run {
            setRegion(region)
            setSize(texture.width * UNIT_SCALE, texture.height * UNIT_SCALE)
            setOriginCenter()
        }
    }

    companion object{
        val mapper = mapperFor<GraphicComponent>()
    }
}