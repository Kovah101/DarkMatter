package com.github.kovah101.darkmatter.screen

import com.github.kovah101.darkmatter.DarkMatter
import com.github.kovah101.darkmatter.assets.SoundAsset
import com.github.kovah101.darkmatter.assets.TextureAsset
import com.github.kovah101.darkmatter.assets.TextureAtlasAsset
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.collections.gdxArrayOf
import ktx.log.debug
import ktx.log.logger

private val LOG = logger<LoadingScreen>()

class LoadingScreen(game: DarkMatter) : DarkMatterScreen(game) {

    override fun show() {
        val old = System.currentTimeMillis()
        // queue asset loading
        val assetRefs = gdxArrayOf(
            TextureAsset.values().map { assets.loadAsync(it.descriptor) },
            TextureAtlasAsset.values().map { assets.loadAsync(it.descriptor) },
            SoundAsset.values().map { assets.loadAsync(it.descriptor) }
        ).flatten()

        // once assets are loaded change to GameScreen
        // async so non blocking main thread
        KtxAsync.launch {
            assetRefs.joinAll()
            LOG.debug { "Time to load assets: ${System.currentTimeMillis() - old}ms" }
            assetsLoaded()
        }

        // setup UI for loading screen
    }

    private fun assetsLoaded() {
        game.addScreen(GameScreen(game))
        game.setScreen<GameScreen>()
        game.removeScreen<LoadingScreen>()
        dispose()
    }

}