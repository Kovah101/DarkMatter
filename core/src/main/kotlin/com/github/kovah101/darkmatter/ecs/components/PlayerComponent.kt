package com.github.kovah101.darkmatter.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

const val MAX_LIFE = 100f
const val MAX_SHIELD = 100f
const val MAX_AMMO = 15


class PlayerComponent : Component, Pool.Poolable {
    var life = MAX_LIFE
    var maxLife = MAX_LIFE
    var shield = 0f
    var maxShield = MAX_SHIELD
    var distance = 0f
    var ammo = MAX_AMMO
    var maxAmmo = MAX_AMMO

    override fun reset() {
        life = MAX_LIFE
        maxLife = MAX_LIFE
        shield = 0f
        maxShield = MAX_SHIELD
        distance = 0f
        ammo = MAX_AMMO
    }

    companion object {
        val mapper = mapperFor<PlayerComponent>()
    }
}