# Event Horizon
Creating a Space themed endless runner game in LibGDX using Kotlin

[Event Horizon Play Store link](https://play.google.com/store/apps/details?id=com.github.kovah101.darkmatter)

https://user-images.githubusercontent.com/23379263/132179423-e3292fae-50c3-42f2-8b1e-9c99d78845c3.mp4

## Summary
Following a tutorial series by QuillRaven on youtube to learn to use LibGDX framework, practise Kotlin & create a real-time game. After following the tutorials to recreate the Dark Matter game I then expanded the base game with new systems, components, graphics, sounds, controls and varying difficulty.

| **Menu** | **Game** | **Controls** |
| ---| ---| ---|
| <img src="https://github.com/Kovah101/Kovah101/blob/main/Event%20Horizon%20Screenshots/EH%20Menu%202.jpeg" width="260"> | <img src="https://github.com/Kovah101/Kovah101/blob/main/Event%20Horizon%20Screenshots/EH%20Gameplay_Moment2.jpg" width="260"> | <img src="https://github.com/Kovah101/Kovah101/blob/main/Event%20Horizon%20Screenshots/EH%20Controls.jpeg" width="260"> |

## Aims
* Learn the LibGDX framework with Ashley Entity-Component-System & Scene 2D frameworks
* Practise & expand use of Kotlin for Android development
* Create & release a real time game on Play Store

## Tutorial Learning
* Basics of viewports & camera views
* Ashley framework & how entities made of specific components interact with different systems 
* Developing Systems - Render, Player, Move, Damage, Debug, Animation, Power-up, Attachment
* Scrolling background, Texture Atlas & Asset Storage to load when neccesary & reduce memory size using GDX Texture packer & custom fonts
* Audio Service to queue and play sounds & music from assets
* Saving & Loading with preferences
* Game Events & how to make systems dynamically change with them
* Scene 2D for UI, Menus & Controls

## Personal Additions
* Enemy System & components for Asteroids - 5 different types with different stats
* Projectile component for Laser, new ammo power-up: new graphics and sounds for each
* Tilt controls to optimise for Mobile play
* Global Difficulty - adjusting spawn times & gravity pull strength
* New Game Events: 

     * Distance - to increment through difficulties
     * Enemy destroyed - bonus points & potential power-up release
* New Spawn patterns for Asteroids & Power-ups for more complexity
* New shader to glow certain Asteroids for power-up indication
* Redesigned Control UI to display power-ups

## Future Goals
* Google Play Services for Highscore board
* Experiment with implementing adverts

## Useful Links
[QuillRaven LibGDX Kotlin Series](https://www.youtube.com/watch?v=7aa6CY7_j9U&list=PLTKHCDn5RKK-8lZmjZoG4rFywN_SLbZR8)
