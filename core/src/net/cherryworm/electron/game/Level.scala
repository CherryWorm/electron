package net.cherryworm.electron.game

import java.util.{Locale, Scanner}

import box2dLight.RayHandler
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.{Body, World}
import com.badlogic.gdx.utils.Disposable
import net.cherryworm.electron.GameScreen
import net.cherryworm.electron.GameScreen._


class Level(gameScreen: GameScreen, fileHandle: FileHandle) extends Disposable {
	
	private val scanner = new Scanner(fileHandle.read())
	scanner.useLocale(Locale.US)
	
	val width = scanner.nextInt()
	val height = scanner.nextInt()
	
	val world = new World(new Vector2(0, 0), true)
	val rayHandler = new RayHandler(world) {
		setAmbientLight(scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat())
		setBlur(true)
	}
	
	var debug = false
	var powerOn = false
	
	val playerStartX = scanner.nextInt()
	val playerStartY = scanner.nextInt()
	
	val player = new Player(this, playerStartX + 0.5f, playerStartY + 0.5f, scanner.nextFloat())
	
	
	def bodies = {
		val bodies = new com.badlogic.gdx.utils.Array[Body](true, world.getBodyCount, classOf[Body])
		world.getBodies(bodies)
		bodies.items
	}
	
	def entities = bodies map (_.getUserData.asInstanceOf[Entity])
	
	
	def load(): Unit = {
		for (i <- -10 to 20) {
			new Wall(this, i + 0.5f, 0.5f)
		}
		for (i <- -10 to 20) {
			new Wall(this, i + 0.5f, 9.5f)
		}
		new Charge(this, -1, 2, 10.5f, 5.5f)
	}
	
	def reset(): Unit = {
		player.body.setTransform(playerStartX + 0.5f, playerStartY + 0.5f, 0)
		player.body.setAngularVelocity(0)
		player.body.setLinearVelocity(0, 0)
	}
	
	def render(batch: SpriteBatch, camera: OrthographicCamera): Unit = {
		batch.begin()
		entities foreach (_.render(batch))
		batch.end()
		
		rayHandler.setCombinedMatrix(camera)
		rayHandler.updateAndRender()
	}
	
	private var accumulator = 0f
	
	def updateWorld(delta: Float): Unit = {
		val frameTime = Math.min(delta, 0.25f)
		accumulator += frameTime
		
		while (accumulator >= TIME_STEP) {
			entities foreach (_.update(delta, powerOn))
			
			world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS)
			accumulator -= TIME_STEP
		}
	}
	
	def processInputs(): Unit = {
		powerOn = Gdx.input.isKeyPressed(Keys.SPACE)
		if (Gdx.input.isKeyJustPressed(Keys.D)) debug = !debug
		if (Gdx.input.isKeyJustPressed(Keys.R)) reset()
	}
	
	override def dispose(): Unit = {
		world.dispose()
		rayHandler.dispose()
	}
	
	scanner.close()
}
