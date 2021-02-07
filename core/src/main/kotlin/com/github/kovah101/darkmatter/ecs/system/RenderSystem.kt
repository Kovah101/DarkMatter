package com.github.kovah101.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.kovah101.darkmatter.ecs.components.GraphicComponent
import com.github.kovah101.darkmatter.ecs.components.PowerUpType
import com.github.kovah101.darkmatter.ecs.components.TransformComponent
import com.github.kovah101.darkmatter.event.*
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.graphics.use
import ktx.log.error
import ktx.log.logger
import kotlin.math.min

private val LOG = logger<RenderSystem>()

class RenderSystem(
    private val batch: Batch,
    private val gameViewport: Viewport,
    private val uiViewport: Viewport,
    backgroundTexture: Texture,
    private val gameEventManager: GameEventManager
) : GameEventListener,
    SortedIteratingSystem(
    allOf(TransformComponent::class, GraphicComponent::class).get(),
    // comparing entities by transform component
    compareBy { entity -> entity[TransformComponent.mapper] }
) {
    private val background = Sprite(backgroundTexture.apply {
        setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
    })
    private val backgroundSpeed = Vector2(0.01f, -0.25f)

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        gameEventManager.addListener(GameEventType.COLLECT_POWER_UP, this)
    }

    override fun removedFromEngine(engine: Engine?) {
        super.removedFromEngine(engine)
        gameEventManager.removeListener(GameEventType.COLLECT_POWER_UP, this)
    }

    override fun update(deltaTime: Float) {
        uiViewport.apply()
        batch.use(uiViewport.camera.combined){
            // render background
            background.run {
                // always return to original background speed
                // over time dictated by 1f/10f
                backgroundSpeed.y = min( -0.25f,
                backgroundSpeed.y + deltaTime * (1f/10f))
                scroll(backgroundSpeed.x * deltaTime, backgroundSpeed.y * deltaTime)
                draw(batch)
            }
        }

        forceSort() //needed when entities move
        gameViewport.apply()

        batch.use(gameViewport.camera.combined) {
            // render entities
            super.update(deltaTime)
        }

    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        //safety check
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "Entity |entity| must have TransformComponent. entity=$entity" }

        val graphic = entity[GraphicComponent.mapper]
        require(graphic != null) { "Entity |entity| must have GraphicComponent. entity=$entity" }

        if (graphic.sprite.texture == null) {
            // if texture is null, log the error and skip
            LOG.error { "Entity has no texture for rendering. entity=$entity" }
            return
        }

        graphic.sprite.run {
            rotation = transform.rotationDeg
            setBounds(
                transform.interpolatedPosition.x,
                transform.interpolatedPosition.y,
                transform.size.x,
                transform.size.y
            )
            draw(batch)
        }
    }

    override fun onEvent(type: GameEventType, data: GameEvent?) {
        if(type == GameEventType.COLLECT_POWER_UP){
            val eventData = data as GameEventCollectPowerUp
            if(eventData.type == PowerUpType.SPEED_1){
                backgroundSpeed.y -= 0.25f
            } else if (eventData.type == PowerUpType.SPEED_2){
                backgroundSpeed.y -= 0.5f
            }
        }
    }
}