package com.github.kovah101.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.github.kovah101.darkmatter.DarkMatter
import ktx.app.KtxScreen
import ktx.log.debug
import ktx.log.logger

private val LOG = logger<SecondScreen>()

class SecondScreen(game: DarkMatter) : DarkMatterScreen(game) {
    override fun show() {
        LOG.debug { "Second screen is shown" }
    }

    override fun render(delta: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            game.setScreen<FirstScreen>()
        }
    }
}