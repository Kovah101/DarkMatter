package com.github.kovah101.darkmatter.ui

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import ktx.i18n.get
import ktx.scene2d.*

private const val TITLE_PADDING = 13f
private const val PADDING = 2.5f
private const val MAX_HIGH_SCORE = 9999

class MenuUI(private val bundle : I18NBundle) {
    val table : KTableWidget
    val startGameButton: TextButton
    val soundButton: ImageButton
    val controlButton: TextButton
    private val highScoreButton: TextButton
    val creditsButton: TextButton
    val quitGameButton: TextButton

    init {
        table = scene2d.table {
            defaults().pad(PADDING).expandX().fillX().colspan(2)

            label(bundle["gameTitle"], LabelStyles.GRADIENT.name) { cell ->
                wrap = true
                setAlignment(Align.center)
                cell.apply {
                    padTop(TITLE_PADDING)
                    padBottom(TITLE_PADDING)
                }
            }
            row()

            startGameButton = textButton(bundle["startGame"], TextButtonStyles.TRANSPARENT.name)
            row()

            soundButton = imageButton(ImageButtonStyles.SOUND_ON_OFF.name).cell(colspan = 1, expandX = false)
            // TODO change buttons to all be transparent
            controlButton = textButton(bundle["control"], TextButtonStyles.TRANSPARENT.name).cell(colspan = 1)
            row()

            highScoreButton = textButton(bundle["highscore", 0], TextButtonStyles.LABEL_TRANSPARENT.name)
            row()

            creditsButton = textButton(bundle["credit"], TextButtonStyles.LABEL_TRANSPARENT.name)
            row()

            quitGameButton = textButton(bundle["quitGame"], TextButtonStyles.TRANSPARENT.name)

            setFillParent(true)
            top()
            pack()

        }
    }

    fun updateHighScore(highscore: Int){
        highScoreButton.label.run {
            text.setLength(0)
            text.append(bundle["highscore", MathUtils.clamp(highscore, 0, MAX_HIGH_SCORE)])
            invalidateHierarchy()
        }
    }
}