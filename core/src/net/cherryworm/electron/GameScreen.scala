package net.cherryworm.electron

import box2dLight.RayHandler
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.{Body, Box2DDebugRenderer, World}
import com.badlogic.gdx.{Gdx, Screen}
import net.cherryworm.electron.GameScreen._
import net.cherryworm.electron.game.{Entity, Player, Wall}

import scala.collection.JavaConversions._

class GameScreen extends Screen {
	
	val world = new World(new Vector2(0, -5.00f), true)
	val rayHandler = new RayHandler(world) {
		setAmbientLight(0.1f, 0.1f, 0.1f, 0.1f)
		setBlur(true)
	}
	
	val debugRenderer = new Box2DDebugRenderer()
	
	val camera = new OrthographicCamera(10f * Gdx.graphics.getWidth / Gdx.graphics.getHeight, 10f)
	camera.position.y = 5
	camera.position.x = camera.viewportWidth / 2f
	
	var accumulator = 0f
	
	override def dispose(): Unit = {
		world.dispose()
		rayHandler.dispose()
		Player.playerTextureRegion.getTexture.dispose()
		Player.playerFixtureDef.shape.dispose()
		Wall.wallTextureRegion.getTexture.dispose()
		Wall.wallFixtureDef.shape.dispose()
	}
	
	override def render(delta: Float): Unit = {
		camera.update()
		Electron.batch.setProjectionMatrix(camera.combined)
		
		updateWorld(delta)
		
		Electron.batch.begin()
		getEntities(world) foreach(_.render(Electron.batch))
		Electron.batch.end()
		
		rayHandler.setCombinedMatrix(camera)
		rayHandler.updateAndRender()
		//debugRenderer.render(world, camera.combined)
	}
	
	override def show(): Unit = {}
	override def hide(): Unit = {}
	
	override def resume(): Unit = {}
	override def pause(): Unit = {}
	
	override def resize(width: Int, height: Int): Unit = {
		camera.viewportHeight = 10f
		camera.viewportWidth = 10f * width / height
		camera.update()
	}
	
	def updateWorld(delta: Float): Unit = {
		val frameTime = Math.min(delta, 0.25f)
		accumulator += frameTime
		while (accumulator >= TIME_STEP) {
			getEntities(world) foreach (_.update(delta))
			world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS)
			accumulator -= TIME_STEP
		}
	}
	
	
	val player = new Player(world, rayHandler)
	for (i <- -10 to 10) {
		new Wall(world, i, 0)
	}
	
}










object GameScreen {
	val TIME_STEP = 1/300f
	val VELOCITY_ITERATIONS = 6
	val POSITION_ITERATIONS = 2
	val LIGHT_RAYS = 500
	
	val PIXELS_PER_UNIT = 16F
	
	def worldToPixel(v: Vector2): (Int, Int) = ((v.x * PIXELS_PER_UNIT).round, (v.y * PIXELS_PER_UNIT).round.toInt)
	
	def getBodies(world: World) = {
		val bodies = new com.badlogic.gdx.utils.Array[Body](world.getBodyCount)
		world.getBodies(bodies)
		bodies
	}
	
	def getEntities(world: World) = getBodies(world) map (_.getUserData.asInstanceOf[Entity])
}
