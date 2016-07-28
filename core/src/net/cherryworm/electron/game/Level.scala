package net.cherryworm.electron.game

import java.util.{Locale, Scanner}

import box2dLight.RayHandler
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d._
import com.badlogic.gdx.utils.Disposable
import net.cherryworm.electron.GameScreen
import net.cherryworm.electron.GameScreen._


class Level(gameScreen: GameScreen, fileHandle: FileHandle) extends Disposable with ContactListener {
	
	val world = new World(new Vector2(0, 0), true)
	world.setContactListener(this)
	val rayHandler = new RayHandler(world) {
		setBlur(true)
	}
	var textureElements = List[TextureElement]()
	
	var debug = false
	var powerOn = false
	
	
	private val scanner = new Scanner(fileHandle.read())
	scanner.useLocale(Locale.US)
	
	val players = new Array[Player](scanner.nextInt())
	val playerStartPositions = new Array[Vector2](players.length)
	
	for (i <- players.indices) {
		val playerStartPosition = new Vector2(scanner.nextInt(), scanner.nextInt())
		playerStartPositions(i) = playerStartPosition
		players(i) = new Player(this, playerStartPosition.x + 0.5f, playerStartPosition.y + 0.5f, scanner.nextFloat())
	}
	
	rayHandler.setAmbientLight(scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat())
	
	val width = scanner.nextInt()
	val height = scanner.nextInt()
	
	def bodies = {
		val bodies = new com.badlogic.gdx.utils.Array[Body](true, world.getBodyCount, classOf[Body])
		world.getBodies(bodies)
		bodies.items
	}
	
	def entities = bodies map (_.getUserData.asInstanceOf[Entity])
	
	
	def load(): Unit = {
		for (y <- 0 until height; x <- 0 until width) {
			scanner.nextInt() match {
				case 0 => new Box(this, x, y, scanner);
				case 1 => textureElements = TextureElement(x, y, scanner.next()) :: textureElements
				case 2 => new Exit(this, x, y, scanner.next())
			}
		}
	}
	
	def reset(): Unit = {
		for (i <- players.indices) {
			val player = players(i)
			val playerStartPosition = playerStartPositions(i)
			player.body.setTransform(playerStartPosition.x + 0.5f, playerStartPosition.y + 0.5f, 0)
			player.body.setAngularVelocity(0)
			player.body.setLinearVelocity(0, 0)
			player.evacuated = false
		}
	}
	
	def render(batch: SpriteBatch, camera: OrthographicCamera): Unit = {
		batch.begin()
		entities foreach (_.render(batch, powerOn))
		textureElements foreach (_.render(batch))
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
		scanner.close()
	}
	
	override def postSolve(contact: Contact, impulse: ContactImpulse) = Unit
	
	override def endContact(contact: Contact) = Unit
	
	override def beginContact(contact: Contact) = {
		(contact.getFixtureA.getUserData, contact.getFixtureB.getUserData) match {
			case (_: Exit, player: Player) => player.evacuate()
			case (player: Player, _: Exit) => player.evacuate()
			case (_, _) =>
		}
	}
	
	override def preSolve(contact: Contact, oldManifold: Manifold) = Unit
	
	def finishGame() = reset()
}
