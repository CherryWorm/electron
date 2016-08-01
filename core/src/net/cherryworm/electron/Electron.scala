package net.cherryworm.electron

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.physics.box2d.Box2D
import com.badlogic.gdx.{Game, Gdx}
import com.github.czyzby.kiwi.log.LoggerService
import com.github.czyzby.kiwi.util.gdx.GdxUtilities
import com.kotcrab.vis.ui.VisUI
import net.cherryworm.electron.leveleditor.LevelEditor


object Electron {
	
	val WIDTH = 800
	val HEIGHT = 450
	
	lazy val batch = new SpriteBatch
	lazy val shapeRenderer = {
		val renderer = new ShapeRenderer
		renderer.setAutoShapeType(true)
		renderer
	}
	lazy val logger = {
		LoggerService.logTime(true)
		LoggerService forClass getClass
	}
	
	lazy val MAIN_MENU = new MainMenu
	lazy val GAME_SCREEN = new GameScreen
	lazy val LEVEL_EDITOR = new LevelEditor
	
	var instance: Electron = _
	
}


import net.cherryworm.electron.Electron._

class Electron extends Game {
	
	instance = this
	
	override def create() = {
		Box2D.init()
		VisUI.load()
		TextureContainer.addTexturesRecursively(Gdx.files.internal("textures/"))
		setScreen(MAIN_MENU)
	}
	
	override def render() = {
		GdxUtilities.clearScreen()
		super.render()
	}
	
	override def dispose() = {
		super.dispose()
		batch.dispose()
		TextureContainer.dispose()
		VisUI.dispose()
	}
	
	
}
