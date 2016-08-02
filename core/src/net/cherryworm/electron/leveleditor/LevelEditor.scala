package net.cherryworm.electron.leveleditor


import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.{Vector2, Vector3}
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx._
import _root_.net.cherryworm.electron.Electron
import _root_.net.cherryworm.electron.game._

/*
	TO-DOs
	- [x] render sidebar
	- [x] drag entities
	- [x] snap to grid
	- [ ] drag'n'drop entities
	- [ ] delete entities by dragging on sidebar
	- [ ] alt-drag to copy entities
	- [ ] render entity data
 */

class LevelEditor extends Screen with InputProcessor {

	val debugRenderer = new Box2DDebugRenderer()

	val camera = new OrthographicCamera(10f * Gdx.graphics.getWidth / Gdx.graphics.getHeight, 10f)
	camera.position.y = 5
	camera.position.x = camera.viewportWidth / 2f

	var level = LevelInfo.fromFileHandle(Gdx.files.internal("levels/1.lvl"))

	var dragEntity: Option[DragEntityActor] = None
	val sidebar = new Sidebar(camera)

	Gdx.input.setInputProcessor(this)

	override def dispose(): Unit = {
		debugRenderer.dispose()
	}

	override def render(delta: Float): Unit = {
		camera.update()

		Electron.batch.setProjectionMatrix(camera.combined)
		Electron.shapeRenderer.setProjectionMatrix(camera.combined)

		level.render(Electron.batch)

		sidebar.render(Electron.batch, Electron.shapeRenderer)

		dragEntity foreach (_.render(Electron.batch))

		if (dragEntity.isDefined) {
			Electron.shapeRenderer.begin()
			level.tiles() foreach ((a) => {
				Electron.shapeRenderer.rect(a.x + 0.25f, a.y + 0.25f, 0.5f, 0.5f)
			})
			Electron.shapeRenderer.end()
		}
	}

	override def show(): Unit = {
		Gdx.input.setInputProcessor(this)
	}

	override def hide() = Unit

	override def resume() = Unit

	override def pause() = Unit

	override def resize(width: Int, height: Int): Unit = {
		camera.viewportHeight = 10f
		camera.viewportWidth = 10f * width / height
		camera.update()
	}

	override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
		val pos3 = camera.unproject(new Vector3(screenX, screenY, 0))
		val pos = new Vector2(pos3.x, pos3.y)
		dragEntity = sidebar.entityAt(pos) map ((spec) => {
			new DragEntityActor(spec)
		})
		true
	}

	override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = {
		val pos3 = camera.unproject(new Vector3(screenX, screenY, 0))
		val pos = new Vector2(pos3.x, pos3.y)
		for (dragEntity <- dragEntity) {
			val snapped = level.tiles() map ((tile) =>
				new Vector2(0.5f, 0.5f).add(tile)
			) find ((tile) => {
				pos.dst(tile) < 0.3f
			}) getOrElse pos
			dragEntity.pos = snapped
		}
		true
	}

	override def touchUp (screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
		for (dragEntity <- dragEntity) {
			val entity = dragEntity.spec.mkNew(dragEntity.pos)
			level = level.addEntity(entity)
		}
		dragEntity = None
		true
	}

	override def mouseMoved(screenX: Int, screenY: Int) = true
	override def keyTyped(character: Char) = true
	override def keyDown(keyCode: Int) = true
	override def keyUp(keyCode: Int) = true
	override def scrolled(amount: Int) = true
}
