package com.github.kovah101.darkmatter

import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Gdx
import com.github.kovah101.darkmatter.screen.FirstScreen
import com.github.kovah101.darkmatter.screen.SecondScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.log.debug
import ktx.log.logger

private val LOG = logger<DarkMatter>()

class DarkMatter : KtxGame<KtxScreen>() {
    override fun create() {
        Gdx.app.logLevel = LOG_DEBUG
        LOG.debug {"Create game instance"}
        // must add screens
        addScreen(FirstScreen(this))
        addScreen(SecondScreen(this))

        setScreen<FirstScreen>()
    }
}