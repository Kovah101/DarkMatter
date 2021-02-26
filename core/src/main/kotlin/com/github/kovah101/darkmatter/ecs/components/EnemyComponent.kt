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
    ASTEROID_CHUNK(75f,100f,100f, -3f, AnimationType.ASTEROID_CHUNK),
    ASTEROID_CHIP(60f, 80f, 80f, -3.75f, AnimationType.ASTEROID_CHIP),
    ASTEROID_SMALL(50f, 50f, 50f, -5f, AnimationType.ASTEROID_SMALL),
    ASTEROID_EGG(100f, 100f, 100f, -2.25f, AnimationType.ASTEROID_EGG),
    ASTEROID_LONG(100f, 100f, 100f, -2.75f, AnimationType.ASTEROID_LONG)
}

class EnemyComponent : Component, Pool.Poolable {
    var type = EnemyType.NONE

    override fun reset() {
        type = EnemyType.NONE
    }

    companion object{
        val mapper = mapperFor<EnemyComponent>()
    }
}