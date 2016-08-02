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

class Level() extends Disposable with ContactListener {
	val world = new World(new Vector2(0, 0), true)
	world.setContactListener(this)
	val rayHandler = new RayHandler(world) {
		setBlur(true)
	}
	var textureElements = List[TextureElement]()
	
	var debug = false
	var powerOn = false
	
	var gameFinished = false

	// FIXME: weniger vars, mehr vals!
	var players = new Array[Player](0)
	var playerStartPositions = new Array[Vector2](players.length)

	var appearance = LevelAppearance.DEFAULT

	def this(fileHandle: FileHandle) {
		this()

		val scanner = new Scanner(fileHandle.read())
		scanner.useLocale(Locale.US)
		def readColor() = new Color(scanner.nextFloat, scanner.nextFloat, scanner.nextFloat, scanner.nextFloat)
		def readLightStrength() = scanner.nextFloat
		def readTexture() = scanner.next
		def readAppearance() = Appearance(readColor(), readLightStrength(), readTexture())
		def readPosition() = new Vector2(scanner.nextInt, scanner.nextInt)
		def readCharge() = scanner.nextFloat
		def readFriction() = scanner.nextFloat
		def readRestitution() = scanner.nextFloat


		//Laden der Lichter und Standarttexturen
		val ambientLightColor = readColor()
		rayHandler.setAmbientLight(ambientLightColor)

		val exitAppearance = readAppearance()

		appearance = LevelAppearance.read(scanner)

		//Laden der Spieler
		players = new Array[Player](scanner.nextInt())
		playerStartPositions = new Array[Vector2](players.length)

		for (i <- players.indices) {
			val playerStartPosition = readPosition()
			playerStartPositions(i) = playerStartPosition
			val charge = readCharge()
			players(i) = new Player(this, playerStartPosition.x, playerStartPosition.y, charge, if(charge > 0) appearance.positivePlayer else appearance.negativePlayer)
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

		scanner.close()
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
		powerOn = (0 to 153 exists Gdx.input.isKeyPressed) || (0 to 4 exists Gdx.input.isButtonPressed) || Gdx.input.isTouched
		if (Gdx.input.isKeyJustPressed(Keys.D)) debug = !debug
		if (Gdx.input.isKeyJustPressed(Keys.R)) reset()
	}

	def addEntity(entity: Entity): Unit = ???
	
	override def dispose(): Unit = {
		world.dispose()
		rayHandler.dispose()
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

	def tiles(): IndexedSeq[Vector2] = {
		// TODO: level width, height
		for (x <- 0 until 30; y <- 0 until 20) yield new Vector2(x, y)
	}
}
