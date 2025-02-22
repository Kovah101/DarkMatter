package com.github.kovah101.darkmatter.ui

import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import com.github.kovah101.darkmatter.V_HEIGHT_PIXELS
import com.github.kovah101.darkmatter.V_WIDTH_PIXELS
import ktx.actors.onClick
import ktx.scene2d.*

private const val PADDING = 7f
private const val DIALOG_WIDTH_SCALE = 0.95f
private const val DIALOG_HEIGHT_SCALE = 0.75f

class TextDialog(
    bundle: I18NBundle,
    textBundleKey: String
) : Dialog("", Scene2DSkin.defaultSkin, WindowStyles.DEFAULT.name) {

    init {
        buttonTable.defaults().padBottom(2f)
        button(scene2d.textButton(bundle["close"], TextButtonStyles.DEFAULT.name).apply {
            labelCell.padLeft(PADDING).padRight(PADDING)
            onClick { hide() }
        })
        buttonTable.pack()

        contentTable.defaults().fill().expand()
        contentTable.add(scene2d.scrollPane(ScrollPaneStyles.DEFAULT.name) {
            setScrollbarsVisible(true)
            fadeScrollBars = false
            variableSizeKnobs = true

            label(bundle[textBundleKey], LabelStyles.DEFAULT.name) {
                wrap = true
                setAlignment(Align.top)
            }
        }).padRight(-PADDING)
        contentTable.pack()

        // to prevent rescaling or rotating
        // avoids additional texture and draw calls
        isTransform = false


    }

    override fun getPrefHeight() = V_HEIGHT_PIXELS * DIALOG_HEIGHT_SCALE

    override fun getPrefWidth() = V_WIDTH_PIXELS * DIALOG_WIDTH_SCALE
}