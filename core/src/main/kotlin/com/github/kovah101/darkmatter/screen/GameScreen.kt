package com.github.kovah101.darkmatter.screen


import com.github.kovah101.darkmatter.DarkMatter
import com.github.kovah101.darkmatter.ecs.components.*
import ktx.ashley.entity
import ktx.ashley.with
import ktx.log.debug
import ktx.log.logger
import java.lang.Float.min

private val LOG = logger<GameScreen>()
private const val MAX_DELTA_TIME = 1/ 20f //used to stop spiral of death

class GameScreen(game: DarkMatter) : DarkMatterScreen(game) {


    override fun show() {
        LOG.debug { "First screen shown" }

        // 1st ship
        engine.entity {
            with<TransformComponent> {
                setInitialPosition(4.2f, 8f, 0f)
            }
            with<MoveComponent>()
            with<GraphicComponent>()
            with<PlayerComponent>()
            with<FacingComponent>()
        }


    }


    override fun render(delta: Float) {
        engine.update(min(MAX_DELTA_TIME, delta))
    }

}
