package com.github.kovah101.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.viewport.FitViewport
import com.github.kovah101.darkmatter.DarkMatter
import com.github.kovah101.darkmatter.UNIT_SCALE
import com.github.kovah101.darkmatter.ecs.components.GraphicComponent
import com.github.kovah101.darkmatter.ecs.components.TransformComponent
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import ktx.graphics.use
import ktx.log.debug
import ktx.log.logger

private val LOG = logger<GameScreen>()

class GameScreen(game: DarkMatter) : DarkMatterScreen(game) {
    private val playerTexture = Texture(Gdx.files.internal("graphics/ship_base.png"))
    private val player = engine.entity {
        with<TransformComponent> {
            position.set(1f, 1f, 0f)

        }
        with<GraphicComponent> {
            sprite.run {
                setRegion(playerTexture)
                setSize(texture.width * UNIT_SCALE, texture.height * UNIT_SCALE)
                setOriginCenter() // used for rotation center
            }
        }
    }
    private val viewport = FitViewport(9f, 16f) // world units

    override fun show() {
        LOG.debug { "First screen shown" }
    }

    override fun render(delta: Float) {
        engine.update(delta)

        viewport.apply()
        batch.use(viewport.camera.combined) { batch ->
            player[GraphicComponent.mapper]?.let { graphic ->
                player[TransformComponent.mapper]?.let { transform ->
                    graphic.sprite.run {
                        rotation = transform.rotationDeg
                        setBounds(transform.position.x, transform.position.y, transform.size.x, transform.size.y)
                        draw(batch)
                    }


                }
            }
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {
        playerTexture.dispose()
        batch.dispose()
    }
}
