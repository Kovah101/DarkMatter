package com.github.kovah101.darkmatter.ui

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.github.kovah101.darkmatter.assets.BitmapFontAsset
import com.github.kovah101.darkmatter.assets.TextureAtlasAsset
import ktx.assets.async.AssetStorage
import ktx.scene2d.Scene2DSkin
import ktx.style.SkinDsl
import ktx.style.label
import ktx.style.skin
import ktx.style.textButton

enum class LabelStyles {
    DEFAULT,
    GRADIENT
}

enum class TextButtonStyles {
    DEFAULT,
    LARGE
}

fun createSkin(assets: AssetStorage){
    val atlas = assets[TextureAtlasAsset.UI.descriptor]
    val gradientFont = assets[BitmapFontAsset.FONT_LARGE_GRADIENT.descriptor]
    val normalFont = assets[BitmapFontAsset.FONT_DEFAULT.descriptor]
    Scene2DSkin.defaultSkin = skin(atlas) { skin ->
        createLabelStyles(normalFont, gradientFont)
        textButton(TextButtonStyles.DEFAULT.name) {
            font = normalFont
            up = skin.getDrawable("frame")
            down = up
        }
        textButton(TextButtonStyles.LARGE.name) {
            font = normalFont
            up = skin.getDrawable("label_frame")
            down = up

        }
    }

}


private fun @SkinDsl Skin.createLabelStyles(
    normalFont: BitmapFont,
    gradientFont: BitmapFont
) {
    label(LabelStyles.DEFAULT.name) {
        font = normalFont
    }
    label(LabelStyles.GRADIENT.name) {
        font = gradientFont
    }
}