package com.github.kovah101.darkmatter.lwjgl3


import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.github.kovah101.darkmatter.DarkMatter
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

/** Launches the desktop (LWJGL3) application.  */

    fun main() {
    Lwjgl3Application(DarkMatter(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("DarkMatter")
        setWindowedMode(9*32, 16*32) // pixels
        setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png")

    })
}
