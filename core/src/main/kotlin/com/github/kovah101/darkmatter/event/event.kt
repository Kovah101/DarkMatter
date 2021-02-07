package com.github.kovah101.darkmatter.event

import com.github.kovah101.darkmatter.ecs.components.PowerUpType
import ktx.collections.GdxSet
import ktx.log.error
import ktx.log.logger
import java.util.*
import javax.swing.text.html.parser.Entity

private val LOG = logger<GameEventManager>()

enum class GameEventType{
    PLAYER_DEATH,
    COLLECT_POWER_UP
}

interface GameEvent

object GameEventPlayerDeath : GameEvent {
    var distance = 0f

    override fun toString() = "GameEventPlayerDeath(distance=$distance)"

}

object GameEventCollectPowerUp : GameEvent {
    lateinit var player : com.badlogic.ashley.core.Entity
    var type = PowerUpType.NONE

    override fun toString() = "GameEventCollectPowerUp(player=$player, type=$type)"
}

interface GameEventListener {
    fun onEvent(type: GameEventType, data:GameEvent? = null)
}

class GameEventManager {
    private val listeners = EnumMap<GameEventType, GdxSet<GameEventListener>>(GameEventType::class.java)

    fun addListener(type: GameEventType, listener: GameEventListener){
        var eventListeners = listeners[type]
        // check if already have eventListeners
        // if null then add to EnumMap of listeners
        if(eventListeners == null){
            eventListeners = GdxSet()
            listeners[type] = eventListeners
        }
        // add listener to relevant listener set
        eventListeners.add(listener)
    }

    fun removeListener(type: GameEventType, listener: GameEventListener){
        var eventListeners = listeners[type]
        if(eventListeners != null) {
            eventListeners.remove(listener)
        }
        else {
            LOG.error { "listener= $listener has not been added to eventListener:$eventListeners or has wrong type:$type" }
        }
    }

    fun removeListener(listener : GameEventListener) {
        listeners.values.forEach { it.remove(listener)}
    }

    //any class that is registered with the manager can dispatch event
    // and notify all relevant listeners
    fun dispatchEvent(type: GameEventType, data: GameEvent? = null){
        listeners[type]?.forEach { it.onEvent(type, data) }
    }

}