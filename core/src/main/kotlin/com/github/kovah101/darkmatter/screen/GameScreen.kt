package com.github.kovah101.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import com.github.kovah101.darkmatter.DarkMatter
import com.github.kovah101.darkmatter.UNIT_SCALE
import com.github.kovah101.darkmatter.ecs.components.*
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import ktx.graphics.use
import ktx.log.debug
import ktx.log.logger

private val LOG = logger<GameScreen>()

class GameScreen(game: DarkMatter) : DarkMatterScreen(game) {


    override fun show() {
        LOG.debug { "First screen shown" }

        // 1st ship
        engine.entity {
            with<TransformComponent> {
                position.set(4.2f, 8f, 0f)
            }
            with<MoveComponent>()
            with<GraphicComponent>()
            with<PlayerComponent>()
            with<FacingComponent>()
        }


    }


    override fun render(delta: Float) {
        engine.update(delta)
    }

}
