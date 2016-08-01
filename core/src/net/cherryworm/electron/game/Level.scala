package net.cherryworm.electron.game

import java.util.{Locale, Scanner}

import box2dLight.RayHandler
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.{Color, OrthographicCamera}
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d._
import com.badlogic.gdx.utils.Disposable

import net.cherryworm.electron.GameScreen._


class Level(fileHandle: FileHandle) extends Disposable with ContactListener {
	
	val world = new World(new Vector2(0, 0), true)
	world.setContactListener(this)
	val rayHandler = new RayHandler(world) {
		setBlur(true)
	}
	var textureElements = List[TextureElement]()
	
	var debug = false
	var powerOn = false
	
	var gameFinished = false
	
	
	private val scanner = new Scanner(fileHandle.read())
	scanner.useLocale(Locale.US)
	private def readColor() = new Color(scanner.nextFloat, scanner.nextFloat, scanner.nextFloat, scanner.nextFloat)
	private def readLightStrength() = scanner.nextFloat
	private def readTexture() = scanner.next
	private def readAppearance() = Appearance(readColor(), readLightStrength(), readTexture())
	private def readPosition() = new Vector2(scanner.nextInt, scanner.nextInt)
	private def readCharge() = scanner.nextFloat
	private def readFriction() = scanner.nextFloat
	private def readRestitution() = scanner.nextFloat
	
	
	//Laden der Lichter und Standarttexturen
	val ambientLightColor = readColor()
	rayHandler.setAmbientLight(ambientLightColor)
	
	val exitAppearance = readAppearance()
	
	val positiveChargeAppearance = readAppearance()
	val neutralChargeAppearance = readAppearance()
	val negativeChargeAppearance = readAppearance()
	
	val positivePlayerAppearance = readAppearance()
	val negativePlayerAppearance = readAppearance()
	
	
	
	//Laden der Spieler
	val players = new Array[Player](scanner.nextInt())
	val playerStartPositions = new Array[Vector2](players.length)
	
	for (i <- players.indices) {
		val playerStartPosition = readPosition()
		playerStartPositions(i) = playerStartPosition
		val charge = readCharge()
		players(i) = new Player(this, playerStartPosition.x, playerStartPosition.y, charge, if(charge > 0) positivePlayerAppearance else negativePlayerAppearance)
	}
	
	
	//Laden des Feldes
	val width = scanner.nextInt()
	val height = scanner.nextInt()
	
	for (y <- 0 until height; x <- 0 until width) {
		scanner.nextInt() match {
			case 0 => new Box(this, x + 0.5f, y + 0.5f, readTexture(), readCharge(), readCharge(), readFriction(), readRestitution());
			case 1 => textureElements = TextureElement(x, y, readTexture()) :: textureElements
			case 2 => new Exit(this, x, y, exitAppearance)
		}
	}
	
	
	
	def bodies = {
		val bodies = new com.badlogic.gdx.utils.Array[Body](true, world.getBodyCount, classOf[Body])
		world.getBodies(bodies)
		bodies.items
	}
	
	def entities = bodies map (_.getUserData.asInstanceOf[Entity])
	
	def reset(): Unit = {
		for (i <- players.indices) {
			val player = players(i)
			val playerStartPosition = playerStartPositions(i)
			player.body.setTransform(playerStartPosition.x + 0.5f, playerStartPosition.y + 0.5f, 0)
			player.reset()
		}
	}
	
	def render(batch: SpriteBatch, camera: OrthographicCamera): Unit = {
		batch.begin()
		textureElements foreach (_.render(batch))
		entities foreach (_.render(batch, powerOn))
		batch.end()
		
		rayHandler.setCombinedMatrix(camera)
		rayHandler.updateAndRender()
	}
	
	private var accumulator = 0f
	
	def updateWorld(delta: Float): Unit = {
		if(gameFinished) {
			finishGame()
			return
		}
		
		val frameTime = Math.min(delta, 0.25f)
		accumulator += frameTime
		
		while (accumulator >= TIME_STEP) {
			entities foreach (_.update(delta, powerOn))
			
			world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS)
			accumulator -= TIME_STEP
		}
	}
	
	def processInputs(): Unit = {
		powerOn = (0 to 153 map Gdx.input.isKeyPressed reduce (_ || _)) || (0 to 4 map Gdx.input.isButtonPressed reduce (_ || _)) || Gdx.input.isTouched
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
	
	def finishGame() = {
		reset()
		gameFinished = false
	}
}
