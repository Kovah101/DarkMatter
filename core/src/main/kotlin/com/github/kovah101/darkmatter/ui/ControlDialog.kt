package com.github.kovah101.darkmatter.ui

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
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

class ControlDialog(
    engine: Engine,
    bundle: I18NBundle,
    textBundleKey: String
) : Dialog("", Scene2DSkin.defaultSkin, WindowStyles.DEFAULT.name) {

    private lateinit var lifePowerUp: Image
    val powerUpTable : Table

    init {
        buttonTable.defaults().padBottom(2f)
        button(scene2d.textButton(bundle["close"], TextButtonStyles.DEFAULT.name).apply {
            labelCell.padLeft(PADDING).padRight(PADDING)
            onClick { hide() }
        })
        buttonTable.pack()

        contentTable.defaults().expand().fill()
        contentTable.add(scene2d.scrollPane(ScrollPaneStyles.DEFAULT.name) {
            setScrollbarsVisible(true)
            fadeScrollBars = false
            variableSizeKnobs = true

            label(bundle[textBundleKey], LabelStyles.DEFAULT.name) {
                wrap = true
                setAlignment(Align.top)
            }
        }).padRight(-96f)
        contentTable.row()
        contentTable.pack()

        powerUpTable = contentTable
        powerUpTable.defaults().fill().expand().padRight(6f).padLeft(6f)
        powerUpTable.add(scene2d.image("life_0"))
        powerUpTable.add(scene2d.image("shield_0"))
        powerUpTable.add(scene2d.image("orb_blue_1"))
        powerUpTable.add(scene2d.image("orb_yellow_1"))
        powerUpTable.pack()



        // to prevent rescaling or rotating
        // avoids additional texture and draw calls
        isTransform = false


    }

    override fun getPrefHeight() = V_HEIGHT_PIXELS * DIALOG_HEIGHT_SCALE

    override fun getPrefWidth() = V_WIDTH_PIXELS * DIALOG_WIDTH_SCALE
}