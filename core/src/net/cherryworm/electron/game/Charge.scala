package net.cherryworm.electron.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.{BodyDef, FixtureDef, PolygonShape}


object Charge {
	
	val COULOMBS_CONSTANT = -8.99e9f
	val FORCE_ADJUSTMENT = 1e-8f
	
	def chargeBodyDef(x: Int, y: Int) = new BodyDef {
		`type` = BodyType.StaticBody
		position.set(x, y)
	}
	
	lazy val chargeFixtureDef = new FixtureDef {
		density = 1f
		friction = 1f
		restitution = 0.5f
		shape = new PolygonShape() {
			setAsBox(1, 1)
		}
	}
	
	lazy val positiveTextureRegion = new TextureRegion(new Texture(Gdx.files.internal("textures/positive_charge.png")))
	lazy val negativeTextureRegion = new TextureRegion(new Texture(Gdx.files.internal("textures/negative_charge.png")))
	lazy val neutralTextureRegion = new TextureRegion(new Texture(Gdx.files.internal("textures/neutral_charge.png")))
	
	def textureFromCharge(x: Int) = x match {
		case 0 => neutralTextureRegion
		case i if i > 0 => positiveTextureRegion
		case i if i < 0 => negativeTextureRegion
	}
}


import net.cherryworm.electron.game.Charge._

class Charge(level: Level, val chargeOff: Int, val chargeOn: Int, x: Int, y: Int) extends Entity(
	level,
	chargeBodyDef(x, y),
	chargeFixtureDef,
	textureFromCharge(chargeOff)
) {
	
	override def update(delta: Float, stateOn: Boolean) = {
		val charge = if (stateOn) chargeOn else chargeOff
		textureRegion = textureFromCharge(charge)
		
		val thisCenter = body.getPosition.cpy() add body.getMassData.center
		val playerCenter = level.player.body.getPosition.cpy() add level.player.body.getMassData.center
		
		val playerToThis = thisCenter.cpy() sub playerCenter
		
		def invert(vector: Vector2) = new Vector2(1f / vector.x, 1f / vector.y)
		def square(vector: Vector2) = vector.cpy() scl vector
		
		val f = (COULOMBS_CONSTANT * FORCE_ADJUSTMENT * charge * level.player.charge) / playerToThis.len2()
		
		level.player.body.applyForceToCenter(playerToThis.cpy().nor scl f, true)
	}
	
}
