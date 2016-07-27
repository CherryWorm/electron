package net.cherryworm.electron.game

import box2dLight.PointLight
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.{Color, Texture}
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.{BodyDef, FixtureDef, PolygonShape}
import net.cherryworm.electron.GameScreen._


object Charge {
	
	val COULOMBS_CONSTANT = -8.99e9f
	val FORCE_ADJUSTMENT = 1e-9f
	
	def chargeBodyDef(x: Float, y: Float) = new BodyDef {
		`type` = BodyType.StaticBody
		position.set(x, y)
	}
	
	lazy val chargeFixtureDef = new FixtureDef {
		density = 0.5f
		friction = 0.5f
		restitution = 0.2f
		shape = new PolygonShape() {
			setAsBox(0.5f, 0.5f)
		}
	}
	
	lazy val positiveTextureRegion = new TextureRegion(new Texture(Gdx.files.internal("textures/positive_charge.png")))
	lazy val negativeTextureRegion = new TextureRegion(new Texture(Gdx.files.internal("textures/negative_charge.png")))
	lazy val neutralTextureRegion = new TextureRegion(new Texture(Gdx.files.internal("textures/neutral_charge.png")))
	
	def textureFromCharge(x: Float) = x match {
		case 0 => neutralTextureRegion
		case i if i > 0 => positiveTextureRegion
		case i if i < 0 => negativeTextureRegion
	}
	
	def colorFromCharge(x: Float) = x match {
		case 0 => Color.BLACK
		case i if i > 0 => Color.GREEN
		case i if i < 0 => Color.RED
	}
}


import net.cherryworm.electron.game.Charge._

class Charge(level: Level, val chargeOff: Float, val chargeOn: Float, x: Float, y: Float) extends Entity(
	level,
	chargeBodyDef(x, y),
	chargeFixtureDef,
	textureFromCharge(chargeOff),
	Option(new PointLight(level.rayHandler, LIGHT_RAYS, colorFromCharge(chargeOff), 3, x, y) {
		setXray(true)
		color.a = 0.2f
		if (chargeOff == 0) setActive(false)
	})
) {
	
	override def update(delta: Float, stateOn: Boolean) = {
		val charge = if (stateOn) chargeOn else chargeOff
		
		textureRegion = textureFromCharge(charge)
		for (light <- light) {
			if (charge == 0)
				light.setActive(false)
			else
				light.setActive(true)
			light.setColor(colorFromCharge(charge))
			light.setDistance(charge.abs * 2)
		}
		
		
		val thisCenter = body.getPosition.cpy() add body.getMassData.center
		val playerCenter = level.player.body.getPosition.cpy() add level.player.body.getMassData.center
		
		val playerToThis = thisCenter.cpy() sub playerCenter
		
		val f = (COULOMBS_CONSTANT * FORCE_ADJUSTMENT * charge * level.player.charge) / playerToThis.len2()
		
		level.player.body.applyForceToCenter(playerToThis.cpy().nor scl f, true)
	}
	
}
