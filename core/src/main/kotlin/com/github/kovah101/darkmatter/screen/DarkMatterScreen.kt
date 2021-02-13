package com.github.kovah101.darkmatter.screen


import com.badlogic.gdx.Preferences
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.kovah101.darkmatter.DarkMatter
import com.github.kovah101.darkmatter.assets.I18NBundleAsset
import com.github.kovah101.darkmatter.audio.AudioService
import com.github.kovah101.darkmatter.event.GameEventManager
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage


abstract class DarkMatterScreen(
    val game: DarkMatter,
    val gameViewport: Viewport = game.gameViewport,
    val uiViewport: Viewport = game.uiViewport,
    val gameEventManager: GameEventManager = game.gameEventManager,
    val assets: AssetStorage = game.assets,
    val audioService: AudioService = game.audioService,
    val preferences: Preferences = game.preferences,
    val stage : Stage = game.stage,
    val bundle: I18NBundle = assets[I18NBundleAsset.DEFAULT.descriptor]
) : KtxScreen {

    override fun resize(width: Int, height: Int) {
        gameViewport.update(width, height, true)
        uiViewport.update(width, height, true)
    }
}
