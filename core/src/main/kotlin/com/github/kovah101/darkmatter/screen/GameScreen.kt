package com.github.kovah101.darkmatter.screen


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.github.kovah101.darkmatter.DarkMatter
import com.github.kovah101.darkmatter.UNIT_SCALE
import com.github.kovah101.darkmatter.V_WIDTH
import com.github.kovah101.darkmatter.ecs.components.*
import com.github.kovah101.darkmatter.ecs.system.DAMAGE_AREA_HEIGHT
import ktx.ashley.entity
import ktx.ashley.with
import ktx.log.debug
import ktx.log.logger
import java.lang.Float.min

private val LOG = logger<GameScreen>()
private const val MAX_DELTA_TIME = 1 / 20f //used to stop spiral of death

class GameScreen(game: DarkMatter) : DarkMatterScreen(game) {


    override fun show() {
        LOG.debug { "First screen shown" }

        // 1st ship
        // TODO Scale ship correctly - see code base if not covered
        val playerShip = engine.entity {
            with<TransformComponent> {
                setInitialPosition(4.5f, 8f, -1f)
            }
            with<MoveComponent>()
            with<GraphicComponent>()
            with<PlayerComponent>()
            with<FacingComponent>()
        }

        // fire effect
        engine.entity {
            with<TransformComponent>()
            with<AttachComponent> {
                entity = playerShip
                offset.set(0.8f * UNIT_SCALE, -6f * UNIT_SCALE)
            }
            with<GraphicComponent>()
            with<AnimationComponent> { type = AnimationType.FIRE }
        }

        // dark matter
        engine.entity {
            with<TransformComponent> {
                size.set(
                    V_WIDTH.toFloat(),
                    DAMAGE_AREA_HEIGHT
                )
            }
            with<AnimationComponent> { type = AnimationType.DARK_MATTER }
            with<GraphicComponent>()
        }


    }


    override fun render(delta: Float) {
        engine.update(min(MAX_DELTA_TIME, delta))
        // TODO Remove Debug lines
        // extra life for debug
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
            // spawn player
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
    }

}
