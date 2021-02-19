package com.github.kovah101.darkmatter.ui

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import ktx.i18n.get
import ktx.scene2d.*

class GameOverUI (private val bundle: I18NBundle){
    val table: KTableWidget
    private val lastScoreButton: TextButton
    private val highScoreButton: TextButton
    val backButton: TextButton
    val soundButton : ImageButton

    // initialise variables
    init {
        table = scene2d.table {
            defaults().pad(10f).expandX().fillX()

            label(bundle["gameTitle"], LabelStyles.GRADIENT.name) { cell ->
                wrap = true
                setAlignment(Align.center)
                cell.apply {
                    padTop(30f)
                    padBottom(15f)
                }
            }
            row()

            lastScoreButton = textButton(bundle["score", 0], TextButtonStyles.LABEL.name)
            row()

            highScoreButton = textButton(bundle["highscore", 0], TextButtonStyles.LABEL.name)
            row()

            backButton = textButton("Restart", LabelStyles.DEFAULT.name)
            row()

            soundButton = imageButton (ImageButtonStyles.SOUND_ON_OFF.name)
            row()


            setFillParent(true)
            top()
            pack()
        }
    }

    fun updateScores(score: Int, highScore: Int) {
        lastScoreButton.label.run {
            text.setLength(0)
            text.append(bundle["score", MathUtils.clamp(score, 0, 999)])
            invalidateHierarchy()
        }
        highScoreButton.label.run {
            text.setLength(0)
            text.append(bundle["highscore", MathUtils.clamp(highScore, 0, 999)])
            invalidateHierarchy()
        }
    }

}