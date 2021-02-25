package com.github.kovah101.darkmatter.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor


enum class EnemyType(
    val damage : Float,
    var health : Float,
    val maxHealth : Float,
    val speed : Float,

){
    NONE(0f,0f,0f, 0f),
    ASTEROID_CHUNK(25f,100f,100f, -3f)
}

class EnemyComponent : Component, Pool.Poolable {
    var type = EnemyType.NONE
    val health = EnemyType.NONE.health

    override fun reset() {
        type = EnemyType.NONE
    }

    companion object{
        val mapper = mapperFor<EnemyComponent>()
    }
}