package net.cherryworm.electron

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.{Gdx, Screen}
import net.cherryworm.electron.game._


object GameScreen {
	val TIME_STEP = 1 / 300f
	val VELOCITY_ITERATIONS = 6
	val POSITION_ITERATIONS = 2
	val LIGHT_RAYS = 500
}


class GameScreen extends Screen {
	
	val debugRenderer = new Box2DDebugRenderer()
	
	val camera = new OrthographicCamera(10f * Gdx.graphics.getWidth / Gdx.graphics.getHeight, 10f)
	camera.position.y = 5
	camera.position.x = camera.viewportWidth / 2f
	
	var accumulator = 0f
	
	val level = new Level(LevelInfo.fromFileHandle(Gdx.files.internal("levels/1.lvl")))
	
	override def dispose(): Unit = {
		debugRenderer.dispose()
		level.dispose()
	}
	
	override def render(delta: Float): Unit = {
		level.processInputs()
		level.updateWorld(delta)
		
		camera.update()
		
		Electron.batch.setProjectionMatrix(camera.combined)
		
		level.render(Electron.batch, camera)
		
		if (level.debug) debugRenderer.render(level.world, camera.combined)
	}
	
	override def show() = Unit
	
	override def hide() = Unit
	
	override def resume() = Unit
	
	override def pause() = Unit
	
	override def resize(width: Int, height: Int): Unit = {
		camera.viewportHeight = 10f
		camera.viewportWidth = 10f * width / height
		camera.update()
	}
	
}
