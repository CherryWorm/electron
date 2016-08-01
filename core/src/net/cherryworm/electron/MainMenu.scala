package net.cherryworm.electron

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.{Event, InputEvent, Stage}
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.{Gdx, Screen}
import com.kotcrab.vis.ui.building.CenteredTableBuilder
import com.kotcrab.vis.ui.widget.VisTextButton
import net.cherryworm.electron.Electron._
import net.cherryworm.electron.Implicits._

class MainMenu extends Screen {
	
	val viewport = new FitViewport(WIDTH, HEIGHT)
	val stage = new Stage(viewport, Electron.batch)
	
	override def resize(width: Int, height: Int) = {
		viewport.update(width, height)
	}
	
	override def dispose() = {
		stage.dispose()
	}
	
	override def render(delta: Float) = {
		stage.act()
		stage.draw()
	}
	
	override def show() = {
		Gdx.input.setInputProcessor(stage)
	}
	
	override def hide() = {
		Gdx.input.setInputProcessor(null)
	}
	
	override def resume() = {
		Gdx.input.setInputProcessor(stage)
	}
	
	override def pause() = {
		Gdx.input.setInputProcessor(null)
	}
	
	
	initUI()
	
	private def initUI(): Unit = {
		val tableBuilder = new CenteredTableBuilder
		tableBuilder.append(new VisTextButton("Start") {
			addListener((e: InputEvent, x: Float, y: Float) => Electron.instance.setScreen(GAME_SCREEN))
		})

    // FIXME: spacing

		tableBuilder.append(new VisTextButton("LevelEditor") {
			addListener((e: InputEvent, x: Float, y: Float) => Electron.instance.setScreen(LEVEL_EDITOR))
		})
		
		val root = tableBuilder.build()
		
		root.setFillParent(true)
		
		stage.addActor(root)
	}
}
