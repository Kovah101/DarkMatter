package com.github.kovah101.darkmatter.assets

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.loaders.BitmapFontLoader
import com.badlogic.gdx.assets.loaders.ShaderProgramLoader
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.I18NBundle

enum class TextureAsset(
    fileName: String,
    directory: String = "graphics",
    val descriptor: AssetDescriptor<Texture> = AssetDescriptor("$directory/$fileName", Texture::class.java)
) {
    BACKGROUND("background.png")
}

enum class TextureAtlasAsset(
    val isSkinAtlas: Boolean,
    fileName: String,
    directory: String = "graphics",
    val descriptor: AssetDescriptor<TextureAtlas> = AssetDescriptor("$directory/$fileName", TextureAtlas::class.java)
) {
    GAME_GRAPHICS(false, "graphics.atlas"),
    UI(true, "ui.atlas")
}

enum class SoundAsset(
    fileName: String,
    directory: String = "sound",
    val descriptor: AssetDescriptor<Sound> = AssetDescriptor("$directory/$fileName", Sound::class.java)
) {
    AMMO("ammo.wav"),
    BOOST_1("boost1.wav"),
    BOOST_2("boost2.wav"),
    LIFE("life.wav"),
    SHIELD("shield.wav"),
    DAMAGE("damage.wav"),
    BLOCK("block.wav"),
    SPAWN("spawn.wav"),
    EXPLOSION("explosion.wav"),
    EXPLOSION_2("explosion2.wav"),
    LASER_1("laser2Loud.wav"),
    LASER_2("laser2.wav"),
    ASTEROID_HIT("asteroidHit.wav")
}

enum class MusicAsset(
    fileName: String,
    directory: String = "music",
    val descriptor: AssetDescriptor<Music> = AssetDescriptor("$directory/$fileName", Music::class.java)
) {
    GAME("game.mp3"),
    GAME_OVER("gameOver.mp3"),
    MENU("menu.mp3")

}

enum class ShaderProgramAsset(
    vertexFileName: String,
    fragmentFileName: String,
    directory: String = "shader",
    val descriptor: AssetDescriptor<ShaderProgram> = AssetDescriptor(
        "$directory/$vertexFileName/$fragmentFileName",
        ShaderProgram::class.java,
        ShaderProgramLoader.ShaderProgramParameter().apply {
            vertexFile = "$directory/$vertexFileName"
            fragmentFile = "$directory/$fragmentFileName"
        }
    )
) {
    OUTLINE("default.vert", "outline.frag")
}

enum class BitmapFontAsset(
    fileName: String,
    directory: String = "ui",
    val descriptor: AssetDescriptor<BitmapFont> = AssetDescriptor(
        "$directory/$fileName",
        BitmapFont::class.java,
        BitmapFontLoader.BitmapFontParameter().apply {
            atlasName = TextureAtlasAsset.UI.descriptor.fileName

        }
    )
) {
    FONT_LARGE_GRADIENT("font11_gradient.fnt"),
    FONT_DEFAULT("font8.fnt")
}

enum class I18NBundleAsset(
    fileName: String,
    directory: String = "i18n",
    val descriptor: AssetDescriptor<I18NBundle> = AssetDescriptor("$directory/$fileName", I18NBundle::class.java)
) {
    DEFAULT("i18n")
}

enum class GlobalDifficulty(
     val pullSpeedMultiplier: Float,
     val minEnemySpawnTimer: Float,
     val maxEnemySpawnTimer: Float
) {
    EXTRA_EASY(0.5f, 1.3f, 1.5f),
    EASY (0.65f, 1.2f, 1.4f),
    MEDIUM(0.85f, 1.1f, 1.3f),
    EXTRA_MEDIUM(1f, 1.0f, 1.2f),
    HARD(1.1f, 0.9f, 1.1f),
    EXTRA_HARD(1.2f, 0.8f, 1.0f)
}