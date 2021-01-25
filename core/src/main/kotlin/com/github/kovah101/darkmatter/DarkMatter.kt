package com.github.kovah101.darkmatter

import com.badlogic.gdx.Game
import com.github.kovah101.darkmatter.FirstScreen

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.  */
class DarkMatter : Game() {
    override fun create() {
        setScreen(FirstScreen())
    }
}