package com.github.kovah101.darkmatter.ui

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import com.github.kovah101.darkmatter.V_HEIGHT_PIXELS
import com.github.kovah101.darkmatter.V_WIDTH_PIXELS
import com.github.kovah101.darkmatter.ecs.components.AnimationComponent
import com.github.kovah101.darkmatter.ecs.components.AnimationType
import com.github.kovah101.darkmatter.ecs.components.GraphicComponent
import com.github.kovah101.darkmatter.ecs.components.PowerUpType
import ktx.actors.onClick
import ktx.ashley.entity
import ktx.ashley.with
import ktx.scene2d.*

private const val PADDING = 7f
private const val DIALOG_WIDTH_SCALE = 0.95f
private const val DIALOG_HEIGHT_SCALE = 0.95f
private const val IMAGE_WIDTH = 20f
private const val IMAGE_BOTTOM_PADDING = 8f
private const val IMAGE_SCALE = 0.9f

class ControlDialog(
    bundle: I18NBundle,
) : Dialog("", Scene2DSkin.defaultSkin, WindowStyles.DEFAULT.name) {

    private lateinit var lifePowerUp: Image

    // val powerUpTable : Table
    //val table: KTableWidget
    //val backButton: Button

    init {
        contentTable.defaults().expand().fillY()
        contentTable.add(scene2d.label(bundle["controls"], LabelStyles.DEFAULT.name) {
            wrap = true
            setAlignment(Align.top)
        }).colspan(5).width(115f).center().padTop(0f)
        contentTable.row()

        contentTable.add(scene2d.image("life_0")).width(IMAGE_WIDTH * IMAGE_SCALE).height(IMAGE_WIDTH * IMAGE_SCALE).padLeft(0f).padBottom(IMAGE_BOTTOM_PADDING)
        contentTable.add(scene2d.image("shield_0")).width(IMAGE_WIDTH * IMAGE_SCALE).height(IMAGE_WIDTH * IMAGE_SCALE).padBottom(IMAGE_BOTTOM_PADDING)
        contentTable.add(scene2d.image("laser_ammo_1")).width(IMAGE_WIDTH * IMAGE_SCALE).height(IMAGE_WIDTH * IMAGE_SCALE).padBottom(IMAGE_BOTTOM_PADDING)
        contentTable.add(scene2d.image("orb_blue_1")).width(IMAGE_WIDTH * IMAGE_SCALE).height(IMAGE_WIDTH * IMAGE_SCALE).padBottom(IMAGE_BOTTOM_PADDING)
        contentTable.add(scene2d.image("orb_yellow_1")).width(IMAGE_WIDTH * IMAGE_SCALE).height(IMAGE_WIDTH * IMAGE_SCALE).padRight(0f).padBottom(IMAGE_BOTTOM_PADDING)
        contentTable.row()

        contentTable.add(scene2d.label(bundle["escape"], LabelStyles.DEFAULT.name) {
            wrap = true
            setAlignment(Align.top)
        }).colspan(5).center().width(115f)
        contentTable.row()

        buttonTable.defaults().padBottom(5f)
        button(scene2d.textButton(bundle["close"], TextButtonStyles.DEFAULT.name).apply {
            labelCell.padLeft(PADDING).padRight(PADDING)
            onClick { hide() }
        })
        buttonTable.pack()

        contentTable.pack()
        isTransform = false
    }


    override fun getPrefHeight() = V_HEIGHT_PIXELS * DIALOG_HEIGHT_SCALE

    override fun getPrefWidth() = V_WIDTH_PIXELS * DIALOG_WIDTH_SCALE
}