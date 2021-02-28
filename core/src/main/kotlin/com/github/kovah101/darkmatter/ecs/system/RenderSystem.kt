package com.github.kovah101.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.kovah101.darkmatter.ecs.components.*
import com.github.kovah101.darkmatter.event.*
import ktx.ashley.allOf
import ktx.ashley.exclude
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
    private val gameEventManager: GameEventManager,
    private val outlineShader: ShaderProgram,
    private val yellowOutlineShader: ShaderProgram
) : GameEventListener,
    SortedIteratingSystem(
        allOf(TransformComponent::class, GraphicComponent::class).get(),
        // comparing entities by transform component
        compareBy { entity -> entity[TransformComponent.mapper] }
    ) {
    // background values
    private val background = Sprite(backgroundTexture.apply {
        setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
    })
    private val backgroundSpeed = Vector2(0.01f, -0.25f)

    // shader values
    private val textureSizeLoc = outlineShader.getUniformLocation("u_textureSize")
    private val outlineColorLoc = outlineShader.getUniformLocation("u_outlineColor")
    private val outlineColorBlue = Color(0f, 113f / 255f, 214f / 255f, 1f)
    private val yellowTextureSizeLoc = yellowOutlineShader.getUniformLocation("u_textureSize")
    private val yellowOutlineColorLoc = yellowOutlineShader.getUniformLocation("u_outlineColor")
    private val outlineColorYellow = Color(214f / 255f, 113f / 255f, 0f, 1f)
    private val playerEntities by lazy {
        engine.getEntitiesFor(allOf(PlayerComponent::class).exclude(RemoveComponent::class).get())
    }
    private val enemyEntities by lazy {
        engine.getEntitiesFor(allOf(EnemyComponent::class).exclude(RemoveComponent::class).get())
    }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        gameEventManager.addListener(GameEvent.CollectPowerUp::class, this)
    }

    override fun removedFromEngine(engine: Engine?) {
        super.removedFromEngine(engine)
        gameEventManager.removeListener(GameEvent.CollectPowerUp::class, this)
    }

    override fun update(deltaTime: Float) {
        uiViewport.apply()
        batch.use(uiViewport.camera.combined) {
            // render background
            background.run {
                // always return to original background speed
                // over time dictated by 1f/10f
                backgroundSpeed.y = min(
                    -0.25f,
                    backgroundSpeed.y + deltaTime * (1f / 10f)
                )
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

        // render outlines of entities
        renderEntityOutlines()

    }

    private fun renderEntityOutlines() {
        batch.use(gameViewport.camera.combined) {
            it.shader = outlineShader
            playerEntities.forEach { entity ->
                renderPlayerOutlines(entity, it)
            }
            it.shader = yellowOutlineShader
            enemyEntities.forEach { entity ->
                renderEnemyOutlines(entity, it)
            }
            it.shader = null
        }
    }


    // TODO new colours applied to all outlines
    private fun renderEnemyOutlines(entity: Entity, batch: Batch) {
        val enemy = entity[EnemyComponent.mapper]
        require(enemy != null) { "Entity |entity| must have a PlayerComponent. entity=$entity" }
        // glow yellow for speed boost 2
        if (enemy.type == EnemyType.ASTEROID_EGG) {
            outlineColorYellow.a = MathUtils.clamp(1f, 0f, 1f)
            yellowOutlineShader.setUniformf(yellowOutlineColorLoc, outlineColorYellow)
            entity[GraphicComponent.mapper]?.let { graphic ->
                graphic.sprite.run {
                    yellowOutlineShader.setUniformf(yellowTextureSizeLoc, texture.width.toFloat(), texture.height.toFloat())
                    draw(batch)
                }
            }
        }
        // glow blue for speed boost 1
        batch.shader = outlineShader
        if (enemy.type == EnemyType.ASTEROID_CHUNK){
            outlineColorBlue.a = MathUtils.clamp(1f, 0f, 1f)
            outlineShader.setUniformf(outlineColorLoc, outlineColorBlue)
            entity[GraphicComponent.mapper]?.let { graphic ->
                graphic.sprite.run {
                    outlineShader.setUniformf(textureSizeLoc, texture.width.toFloat(), texture.height.toFloat())
                    draw(batch)
                }
            }
        }
    }

    private fun renderPlayerOutlines(entity: Entity, batch: Batch) {
        val player = entity[PlayerComponent.mapper]
        require(player != null) { "Entity |entity| must have a PlayerComponent. entity=$entity" }

        if (player.shield > 0f) {
            outlineColorBlue.a = MathUtils.clamp(player.shield / player.maxShield, 0f, 1f)
            outlineShader.setUniformf(outlineColorLoc, outlineColorBlue)
            entity[GraphicComponent.mapper]?.let { graphic ->
                graphic.sprite.run {
                    outlineShader.setUniformf(textureSizeLoc, texture.width.toFloat(), texture.height.toFloat())
                    draw(batch)
                }
            }
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

    override fun onEvent(event: GameEvent) {
        val powerUpEvent = event as GameEvent.CollectPowerUp
        if (powerUpEvent.type == PowerUpType.SPEED_1) {
            backgroundSpeed.y -= 0.25f
        } else if (powerUpEvent.type == PowerUpType.SPEED_2) {
            backgroundSpeed.y -= 0.5f
        }
    }
}
