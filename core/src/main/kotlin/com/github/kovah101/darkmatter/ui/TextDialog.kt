package com.github.kovah101.darkmatter.ui

import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.utils.I18NBundle
import ktx.scene2d.Scene2DSkin

class TextDialog(
    bundle: I18NBundle,
    textBundleKey : String
) : Dialog("", Scene2DSkin.defaultSkin, WindowStyles.DEFAULT.name) {
}