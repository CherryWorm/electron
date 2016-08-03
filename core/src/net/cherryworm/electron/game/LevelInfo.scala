package net.cherryworm.electron.game

import java.util.{Locale, Scanner}

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2

import scala.reflect.ClassTag


case class LevelInfo(entities: List[EntityInfo], appearance: LevelAppearance, width: Int, height: Int) {
	def this() {
		this(
			entities = Nil,
			appearance = LevelAppearance.DEFAULT,
			width = 30,
			height = 20
		)
	}

	def entitiesOf[T <: EntityInfo]()(implicit ev: ClassTag[T]): List[T] = {
		entities collect { case e: T => e }
	}

	def addEntity(entity: EntityInfo): LevelInfo = {
		val filtered = if (!entity.isInstanceOf[PlayerInfo]) {
			entities filter (_.position.cpy().sub(entity.position).len() > 0.3)
		} else {
			entities
		}
		new LevelInfo(filtered ++ (entity :: Nil), appearance, width, height)
	}

	def grabEntity(pos: Vector2): Option[(EntityInfo, LevelInfo)] = {
		for (e <- entities find (_.position.cpy().sub(pos).len() < 0.3)) {
			val filtered = entities filter (_ != e)
			val levelInfo = new LevelInfo(filtered, appearance, width, height)
			return Some((e, levelInfo))
		}

		None
	}

	def tiles(): IndexedSeq[Vector2] = {
		for (x <- 0 until width; y <- 0 until height) yield new Vector2(x, y)
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

		val players = scanner.nextInt()

		var entities: List[EntityInfo] = ((0 until players) map ((_: Int) => {
			val playerStartPosition = readPosition()
			val charge = readCharge()
			PlayerInfo(playerStartPosition, charge)
		})).toList


		// Laden des Feldes
		val width = scanner.nextInt()
		val height = scanner.nextInt()

		for (y <- 0 until height; x <- 0 until width) {
			val entityInfo = scanner.nextInt() match {
				case 0 => BoxInfo(new Vector2(x, y), readTexture(), readCharge(), readCharge(), readFriction(), readRestitution());
				case 1 => TextureElementInfo(new Vector2(x, y), readTexture())
				case 2 => ExitInfo(new Vector2(x, y))
			}
			entities = entities :+ entityInfo
		}

		scanner.close()

		new LevelInfo (
			entities = entities,
			appearance = appearance,
			width = width,
			height = height
		)
	}
}
