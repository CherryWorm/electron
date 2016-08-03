package net.cherryworm.electron.game

import box2dLight.PointLight
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.{BodyDef, FixtureDef, PolygonShape}
import net.cherryworm.electron.TextureContainer
import net.cherryworm.electron.leveleditor.EntitySpec

object Exit {
	
	val fixtureDef = new FixtureDef {
		shape = new PolygonShape() {
			setAsBox(0.5f, 0.5f)
		}
		isSensor = true
	}
	
	def bodyDef(x: Float, y: Float) = new BodyDef {
		`type` = BodyType.StaticBody
		position.set(x, y)
	}
	
}

object ExitSpec extends EntitySpec {
	override val textureID = "exit"
	override def mkNew(pos: Vector2): ExitInfo = ???
}

case class ExitInfo(pos: Vector2) extends EntityInfo {
	override val texture = TextureContainer.getTexture("exit")
	override val position = pos
}

import net.cherryworm.electron.GameScreen._
import net.cherryworm.electron.game.Exit._

class Exit(level: Level, x: Int, y: Int, appearance: Appearance) extends Entity(
	level,
	bodyDef(x + 0.5f, y + 0.5f),
	fixtureDef,
	TextureContainer(appearance.texture),
	Option(new PointLight(level.rayHandler, LIGHT_RAYS, appearance.lightColor, appearance.lightStrength, 0, 0) {
		setXray(true)
	})
) {
	override def update(delta: Float, stateOn: Boolean) = Unit
	
	
}
