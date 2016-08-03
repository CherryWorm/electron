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

class Level(info: LevelInfo) extends Disposable with ContactListener {
	val world = new World(new Vector2(0, 0), true)
	world.setContactListener(this)
	val rayHandler = new RayHandler(world) {
		setBlur(true)
	}
	var textureElements = List[TextureElement]()
	
	var debug = false
	var powerOn = false
	
	var gameFinished = false

	val appearance = info.appearance

	rayHandler.setAmbientLight(appearance.ambientLightColor)

	val playerInfos: List[PlayerInfo] = info.entitiesOf[PlayerInfo]()
	println(playerInfos(0))

	val players = new Array[Player](playerInfos.length)
	val playerStartPositions = new Array[Vector2](players.length)

	for (i <- players.indices) {
		val pInfo = playerInfos(i)
		playerStartPositions(i) = pInfo.position
		players(i) = new Player(this, pInfo.pos.x, pInfo.pos.y, pInfo.charge, if(pInfo.charge > 0) appearance.positivePlayer else appearance.negativePlayer)
	}

	val width = info.width
	val height = info.height

	info.entitiesOf[BoxInfo]() foreach ((b) => {
		new Box(this, b.pos.x + 0.5f, b.pos.y + 0.5f, b.textureID, b.chargeOff, b.chargeOn, b.friction, b.restitution)
	})

	info.entitiesOf[TextureElementInfo]() foreach ((e) => {
		textureElements = TextureElement(e.pos.x.asInstanceOf[Int], e.pos.y.asInstanceOf[Int], e.textureID) :: textureElements
	})


	info.entitiesOf[ExitInfo]() foreach ((e) => {
		new Exit(this, e.pos.x.asInstanceOf[Int], e.pos.y.asInstanceOf[Int], appearance.exit)
	})

	
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
}
