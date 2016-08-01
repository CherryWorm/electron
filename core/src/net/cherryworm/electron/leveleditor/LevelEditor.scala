package net.cherryworm.electron.leveleditor


import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.{Gdx, Input, InputProcessor, Screen}
import net.cherryworm.electron.Electron
import net.cherryworm.electron.game._

/*
	TO-DOs
	- [ ] render entity infos
	- [ ] render sidebar
	- [ ] drag'n'drop entities
	- [ ] delete entities by dragging on sidebar
	- [ ] alt-drag to copy entities
 */

class LevelEditor extends Screen with InputProcessor {

	val debugRenderer = new Box2DDebugRenderer()

	val camera = new OrthographicCamera(10f * Gdx.graphics.getWidth / Gdx.graphics.getHeight, 10f)
	camera.position.y = 5
	camera.position.x = camera.viewportWidth / 2f

	val level = new Level(Gdx.files.internal("levels/1.lvl"))

	val dragEntity: Option[DragEntityActor] = None
	val sidebar = new Sidebar(camera)

	def render_props(batch: SpriteBatch): Unit = {
		Electron.batch.begin()
		level.entities foreach (_.renderProps(Electron.batch))
		Electron.batch.end()
	}

	override def dispose(): Unit = {
		debugRenderer.dispose()
		level.dispose()
	}

	override def render(delta: Float): Unit = {

		level.updateWorld(delta)
		camera.update()

		Electron.batch.setProjectionMatrix(camera.combined)
		Electron.shapeRenderer.setProjectionMatrix(camera.combined)

		level.render(Electron.batch, camera)

		render_props(Electron.batch)

		sidebar.render(Electron.batch, Electron.shapeRenderer)

		dragEntity foreach (_.render(Electron.batch))

		if (dragEntity.isDefined) {
			Electron.shapeRenderer.begin()
			level.tiles() foreach ((a) => {
				println(a)
				Electron.shapeRenderer.rect(a.x + 0.25f, a.y + 0.25f, 0.5f, 0.5f)
			})
			Electron.shapeRenderer.end()
		}


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

	override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
		true
	}

	override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = {
		val pos = camera.unproject(new Vector3(screenX, screenY, 0))
		dragEntity foreach ((e) => {
			e.x = pos.x
			e.y = pos.y
		})
		true
	}

	override def touchUp (screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
		true
	}

	override def mouseMoved(screenX: Int, screenY: Int): Boolean = {
		true
	}

	override def keyTyped(character: Char): Boolean = {
		true
	}

	override def keyDown(keycode: Int): Boolean = {
		true
	}

	override def keyUp(keycode: Int): Boolean = {
		true
	}

	override def scrolled(amount: Int): Boolean = {
		true
	}
}
