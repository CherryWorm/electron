package net.cherryworm.electron

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.github.czyzby.kiwi.log.LoggerService
import com.github.czyzby.kiwi.util.gdx.GdxUtilities
import com.kotcrab.vis.ui.VisUI


object Electron {
	
	val WIDTH = 1920
	val HEIGHT = 1080
	
	lazy val batch = new SpriteBatch
	lazy val logger = {
		LoggerService.logTime(true)
		LoggerService forClass getClass
	}
	
	lazy val MAIN_MENU = new MainMenu
	lazy val GAME_SCREEN = new GameScreen
	
	var instance: Electron = null
	
}


import net.cherryworm.electron.Electron._

class Electron extends Game {
	
	instance = this
	
	override def create() = {
		VisUI.load()
		setScreen(MAIN_MENU)
	}
	
	override def render() = {
		GdxUtilities.clearScreen()
		super.render()
	}
	
	override def dispose() = {
		super.dispose()
		batch.dispose()
		VisUI.dispose()
	}
	
	
}
