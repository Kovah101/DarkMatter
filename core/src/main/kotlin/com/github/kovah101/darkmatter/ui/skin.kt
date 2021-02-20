package com.github.kovah101.darkmatter.ui

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.github.kovah101.darkmatter.assets.BitmapFontAsset
import com.github.kovah101.darkmatter.assets.TextureAtlasAsset
import ktx.assets.async.AssetStorage
import ktx.scene2d.Scene2DSkin
import ktx.style.*

enum class LabelStyles {
    DEFAULT,
    GRADIENT
}

enum class TextButtonStyles {
    DEFAULT,
    LABEL,
    TRANSPARENT,
    LABEL_TRANSPARENT
}

enum class ImageButtonStyles {
    SOUND_ON_OFF
}

enum class WindowStyles {
    DEFAULT
}

enum class ScrollPaneStyles {
    DEFAULT
}

fun createSkin(assets: AssetStorage){
    val atlas = assets[TextureAtlasAsset.UI.descriptor]
    val gradientFont = assets[BitmapFontAsset.FONT_LARGE_GRADIENT.descriptor]
    val normalFont = assets[BitmapFontAsset.FONT_DEFAULT.descriptor]
    Scene2DSkin.defaultSkin = skin(atlas) { skin ->
        createLabelStyles(normalFont, gradientFont)
        createTextButtonStyles(normalFont, skin)
        createImageButtons(skin)
        createWindowStyle(skin, normalFont)
        createScrollPaneStyles(skin)
    }

}

private fun @SkinDsl Skin.createScrollPaneStyles(skin: Skin) {
    scrollPane(ScrollPaneStyles.DEFAULT.name) {
        vScroll = skin.getDrawable("scroll_v")
        vScrollKnob = skin.getDrawable("scroll_knob")
    }
}

private fun @SkinDsl Skin.createWindowStyle(
    skin: Skin,
    normalFont: BitmapFont
) {
    window(WindowStyles.DEFAULT.name) {
        background = skin.getDrawable("frame")
        titleFont = normalFont
    }
}

private fun @SkinDsl Skin.createImageButtons(skin: Skin) {
    imageButton(ImageButtonStyles.SOUND_ON_OFF.name) {
        imageUp = skin.getDrawable("sound")
        imageChecked = skin.getDrawable("no_sound")
        imageDown = imageChecked
        up = skin.getDrawable("frame")
        down = up
    }
}


private fun @SkinDsl Skin.createTextButtonStyles(
    normalFont: BitmapFont,
    skin: Skin
) {
    textButton(TextButtonStyles.DEFAULT.name) {
        font = normalFont
        up = skin.getDrawable("frame")
        down = up
    }
    textButton(TextButtonStyles.LABEL.name) {
        font = normalFont
        up = skin.getDrawable("label_frame")
        down = up
    }
    textButton(TextButtonStyles.TRANSPARENT.name) {
        font = normalFont
        up = skin.getDrawable("frame_transparent")
        down = up
    }
    textButton(TextButtonStyles.LABEL_TRANSPARENT.name) {
        font = normalFont
        up = skin.getDrawable("label_frame_transparent")
        down = up
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