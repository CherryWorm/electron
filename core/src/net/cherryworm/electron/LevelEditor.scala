package net.cherryworm.electron

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.{Gdx, Screen}
import net.cherryworm.electron.game._

/*
	TO-DOs
	- [ ] render entity infos
	- [ ] render sidebar
	- [ ] drag'n'drop entities
	- [ ] delete entities by dragging on sidebar
	- [ ] alt-drag to copy entities
 */

class LevelEditor extends Screen {

	val debugRenderer = new Box2DDebugRenderer()

	val camera = new OrthographicCamera(10f * Gdx.graphics.getWidth / Gdx.graphics.getHeight, 10f)
	camera.position.y = 5
	camera.position.x = camera.viewportWidth / 2f

	val level = new Level(Gdx.files.internal("levels/1.lvl"))

	// TODO
	val dragEntity: Option[Unit] = None

	def render_sidebar(): Unit = {

	}

	override def dispose(): Unit = {
		debugRenderer.dispose()
		level.dispose()
	}

	override def render(delta: Float): Unit = {

		level.updateWorld(delta)
		camera.update()

		Electron.batch.setProjectionMatrix(camera.combined)

		level.render(Electron.batch, camera)

		Electron.batch.begin()
		level.textureElements foreach (_.render(Electron.batch))
		level.entities foreach (_.renderProps(Electron.batch))
		Electron.batch.end()

		render_sidebar()

		// TODO
		dragEntity.foreach((a) => {})

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
