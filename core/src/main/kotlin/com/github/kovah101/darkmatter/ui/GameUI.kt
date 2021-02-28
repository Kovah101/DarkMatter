package com.github.kovah101.darkmatter.ui

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import com.github.kovah101.darkmatter.V_HEIGHT_PIXELS
import com.github.kovah101.darkmatter.V_WIDTH_PIXELS
import ktx.actors.plusAssign
import ktx.scene2d.image
import ktx.scene2d.label
import ktx.scene2d.scene2d
import kotlin.math.roundToInt

private const val PADDING = 2f
private const val MIN_SPEED = -99f
private const val MAX_SPEED = 999f
private const val MAX_DISTANCE = 999999f
private const val GAME_HUD_AREA_HEIGHT = 10f
private const val GAME_HUD_SMALL_AREA_WIDTH = 24f
private const val GAME_HUD_LARGE_AREA_WIDTH = 48f
private const val GAME_HUD_BORDER_SIZE_X = 7f
private const val GAME_HUD_BORDER_SIZE_Y = 6f

class GameUI(private val bundle: I18NBundle) : Group() {

    private val lifeBarImage = scene2d.image("life_bar") {
        width = GAME_HUD_SMALL_AREA_WIDTH
        height = GAME_HUD_AREA_HEIGHT
    }
    private val shieldBarImage = scene2d.image("shield_bar") {
        width = GAME_HUD_SMALL_AREA_WIDTH
        height = GAME_HUD_AREA_HEIGHT
    }
    private val distanceLabel = scene2d.label("0", LabelStyles.DEFAULT.name) {
        width = GAME_HUD_LARGE_AREA_WIDTH
        setAlignment(Align.center)
    }
    private val speedLabel = scene2d.label("0", LabelStyles.DEFAULT.name) {
        width = GAME_HUD_SMALL_AREA_WIDTH
        setAlignment(Align.center)
    }
    private val ammoBarImage = scene2d.image("life_bar_blue") {
        width = GAME_HUD_SMALL_AREA_WIDTH
        height = GAME_HUD_AREA_HEIGHT
    }


    init {
        // top game hud

        var gameHudX: Float
        var gameHudHeight: Float
        var gameHudWidth: Float

        this += scene2d.image("game_hud") {
            gameHudX = V_WIDTH_PIXELS * 0.5f - width * 0.5f
            x = gameHudX
            y = V_HEIGHT_PIXELS - height - PADDING
            gameHudHeight = y
            gameHudWidth = width


        }

        this += lifeBarImage.apply {
            setPosition(
                gameHudX + GAME_HUD_BORDER_SIZE_X * 0.9f,
                gameHudHeight + height * 0.5f
            )
        }
        this += shieldBarImage.apply {
            setPosition(
                gameHudX + GAME_HUD_BORDER_SIZE_X * 0.9f,
                gameHudHeight + height * 0.5f
            )
        }
        this += distanceLabel.apply {
            setPosition(
                gameHudX + gameHudWidth * 0.5f - GAME_HUD_LARGE_AREA_WIDTH * 0.50f,
                gameHudHeight + GAME_HUD_BORDER_SIZE_Y
            )
        }
        this += ammoBarImage.apply {
            setPosition(
                gameHudX + gameHudWidth - GAME_HUD_BORDER_SIZE_X - GAME_HUD_SMALL_AREA_WIDTH,
                gameHudHeight + GAME_HUD_BORDER_SIZE_Y
            )
        }
    }

    fun updateScore(distance: Float, bonusScore: Float) {
        distanceLabel.run {
            text.setLength(0)
            var totalScore = distance * 10 + bonusScore
            text.append(MathUtils.clamp(totalScore, 0f, MAX_DISTANCE).roundToInt())
            invalidateHierarchy()
        }
    }

    fun updateLife(life: Float, maxLife: Float) {
        lifeBarImage.scaleX = MathUtils.clamp(life / maxLife, 0f, 1f)
    }

    fun updateShield(shield: Float, maxShield: Float) {
        shieldBarImage.color.a = MathUtils.clamp(shield / maxShield, 0f, 1f)
    }

    fun updateAmmo(ammo: Float, maxAmmo : Float){
        ammoBarImage.scaleX = MathUtils.clamp(ammo/maxAmmo, 0f, 1f)
    }
}