package com.github.kovah101.darkmatter.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor


enum class EnemyType(
    val damage : Float,
    var health : Float,
    val maxHealth : Float,
    val speed : Float,
    val animationType: AnimationType

){
    NONE(0f,0f,0f, 0f, AnimationType.NONE),
    ASTEROID_CHUNK(25f,100f,100f, -3f, AnimationType.ASTEROID_CHUNK)
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