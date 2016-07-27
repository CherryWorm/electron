package net.cherryworm.electron.game

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.{BodyDef, FixtureDef, PolygonShape}

object Element {
	val shape = new PolygonShape() {
		setAsBox(0.5f, 0.5f)
	}
	
	def bodyDef(x: Float, y: Float) = new BodyDef {
		`type` = BodyType.StaticBody
		position.set(x, y)
	}
}

import net.cherryworm.electron.game.Element._

class Element(level: Level, x: Float, y: Float, textureId: Int, chargeOff: Float, chargeOn: Float, frictionParam: Float, restitutionParam: Float) extends Entity(
	level,
	bodyDef(x, y),
	new FixtureDef {
		density = 1
		friction = frictionParam
		restitution = restitutionParam
		shape = Element.shape
	},

) {
	
}
