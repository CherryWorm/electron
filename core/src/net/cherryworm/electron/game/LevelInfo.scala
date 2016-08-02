package net.cherryworm.electron.game

import java.util.{Locale, Scanner}

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2


case class LevelInfo(entities: List[EntityInfo], appearance: LevelAppearance) {
	def this() {
		this(
			entities = Nil,
			appearance = LevelAppearance.DEFAULT
		)
	}

	def players(): List[PlayerInfo] = ???

	def addEntity(entity: EntityInfo): LevelInfo = {
		new LevelInfo(entities ++ (entity :: Nil), appearance)
	}

	def tiles(): IndexedSeq[Vector2] = {
		// TODO: level width, height
		for (x <- 0 until 30; y <- 0 until 20) yield new Vector2(x, y)
	}

	def render(batch: SpriteBatch): Unit = {
		batch.begin()
		entities foreach (_.render(batch))
		batch.end()
	}
}

object LevelInfo {
	def fromFileHandle(fileHandle: FileHandle): LevelInfo = {
		val scanner = new Scanner(fileHandle.read())
		scanner.useLocale(Locale.US)
		def readTexture() = scanner.next
		def readPosition() = new Vector2(scanner.nextInt, scanner.nextInt)
		def readCharge() = scanner.nextFloat
		def readFriction() = scanner.nextFloat
		def readRestitution() = scanner.nextFloat


		val appearance = LevelAppearance.read(scanner)

		// Laden der Spieler
		val players = new Array[EntityInfo](scanner.nextInt())
		val playerStartPositions = new Array[Vector2](players.length)

		for (i <- players.indices) {
			val playerStartPosition = readPosition()
			playerStartPositions(i) = playerStartPosition
			val charge = readCharge()
			players(i) = new PlayerInfo(playerStartPosition, charge)
		}


		// Laden des Feldes
		val width = scanner.nextInt()
		val height = scanner.nextInt()

		var entities: List[EntityInfo] = Nil

		for (y <- 0 until height; x <- 0 until width) {
			val entityInfo = scanner.nextInt() match {
				case 0 => new BoxInfo(new Vector2(x, y), readTexture(), readCharge(), readCharge(), readFriction(), readRestitution());
				case 1 => new TextureElementInfo(new Vector2(x, y), readTexture())
				case 2 => new ExitInfo(new Vector2(x, y))
			}
			entities = entities ++ (entityInfo :: Nil)
		}

		scanner.close()

		new LevelInfo (
			entities = entities,
			appearance = appearance
		)
	}
}
