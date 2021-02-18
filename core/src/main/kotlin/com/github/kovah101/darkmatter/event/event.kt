package com.github.kovah101.darkmatter.event

import com.badlogic.gdx.utils.ObjectMap
import com.github.kovah101.darkmatter.ecs.components.PowerUpType
import ktx.collections.GdxSet
import ktx.log.error
import ktx.log.logger
import java.util.*
import javax.swing.text.html.parser.Entity
import kotlin.reflect.KClass

private val LOG = logger<GameEventManager>()

sealed class GameEvent {
    object PlayerDeath : GameEvent() {
        var distance = 0f

        override fun toString() = "PlayerDeath(distance=$distance)"
    }

    object CollectPowerUp : GameEvent() {
        lateinit var player: com.badlogic.ashley.core.Entity
        var type = PowerUpType.NONE

        override fun toString() = "CollectPowerUp(player=$player, type=$type)"
    }

    object PlayerHit : GameEvent(){
        lateinit var player : com.badlogic.ashley.core.Entity
        var life = 0f
        var maxLife = 0f

        override fun toString(): String = "PlayerHit(player:$player, life=$life, maxLife=$maxLife"
    }

    object PlayerBlock: GameEvent(){
        var shield = 0f
        var maxShield = 0f

        override fun toString(): String = "PlayerBlock(shield:$shield, maxShield=$maxShield"
    }

    object PlayerMove : GameEvent() {
        var distance = 0f
        var speed = 0f

        override fun toString() = "PlayerMove(distance=$distance,speed=$speed)"
    }
}


interface GameEventListener {
    fun onEvent(event: GameEvent)
}

class GameEventManager {
    private val listeners = ObjectMap<KClass<out GameEvent>, GdxSet<GameEventListener>>()

    fun addListener(type: KClass<out GameEvent>, listener: GameEventListener) {
        var eventListeners = listeners[type]
        // check if already have eventListeners
        // if null then add to EnumMap of listeners
        if (eventListeners == null) {
            eventListeners = GdxSet()
            listeners.put(type, eventListeners)
        }
        // add listener to relevant listener set
        eventListeners.add(listener)
    }

    fun removeListener(type: KClass<out GameEvent>, listener: GameEventListener) {
        listeners[type]?.remove(listener)
    }

    fun removeListener(listener: GameEventListener) {
        listeners.values().forEach { it.remove(listener) }
    }

    //any class that is registered with the manager can dispatch event
    // and notify all relevant listeners
    fun dispatchEvent(event: GameEvent) {
        listeners[event::class]?.forEach { it.onEvent(event) }

    }
}