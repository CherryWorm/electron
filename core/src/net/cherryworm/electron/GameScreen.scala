package net.cherryworm.electron

import com.badlogic.gdx.Screen
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.{Body, World}
import net.cherryworm.electron.game.{Entity, Player, Wall}
import GameScreen._
import box2dLight.RayHandler
import com.badlogic.gdx.graphics.Color

import scala.collection.JavaConversions._

class GameScreen extends Screen {
	
	val world = new World(new Vector2(0, -9.81f), true)
	val rayHandler = new RayHandler(world) {
		setShadows(false)
		//setAmbientLight(0x909AD497)
	}
	var accumulator = 0f
	
	override def dispose(): Unit = world.dispose()
	
	override def render(delta: Float): Unit = {
		updateWorld(delta)
		
		Electron.batch.begin()
		getEntities(world) foreach(_.render(Electron.batch))
		Electron.batch.end()
	}
	
	override def show(): Unit = {}
	override def hide(): Unit = {}
	
	override def resume(): Unit = {}
	override def pause(): Unit = {}
	
	override def resize(width: Int, height: Int): Unit = {}
	
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
	for(i <- -5 to 5) {
		new Wall(world, 5, 0)
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
