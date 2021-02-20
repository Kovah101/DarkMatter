package com.github.kovah101.darkmatter.screen

import com.badlogic.ashley.core.Engine
import com.github.kovah101.darkmatter.DarkMatter

class MenuScreen(
    game: DarkMatter,
    private val engine: Engine = game.engine
) : DarkMatterScreen(game) {
}