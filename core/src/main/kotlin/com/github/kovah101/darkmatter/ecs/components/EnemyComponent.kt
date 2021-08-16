package com.github.kovah101.darkmatter.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.github.kovah101.darkmatter.assets.SoundAsset
import ktx.ashley.mapperFor


enum class EnemyType(
    val damage : Float,
    var health : Float,
    val maxHealth : Float,
    val speed : Float,
    val animationType: AnimationType,
    val soundAsset: SoundAsset = SoundAsset.EXPLOSION_2

){
    NONE(0f,0f,0f, 0f, AnimationType.NONE),
    ASTEROID_CHUNK(25f,90f,90f, -3f, AnimationType.ASTEROID_CHUNK),
    ASTEROID_CHIP(25f, 80f, 80f, -3.75f, AnimationType.ASTEROID_CHIP),
    ASTEROID_SMALL(10f, 50f, 50f, -5f, AnimationType.ASTEROID_SMALL),
    ASTEROID_EGG(50f, 90f, 90f, -2.25f, AnimationType.ASTEROID_EGG),
    ASTEROID_LONG(50f, 90f, 90f, -2.75f, AnimationType.ASTEROID_LONG)
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