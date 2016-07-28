package net.cherryworm.electron.game

import java.util.Scanner

import box2dLight.PointLight
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.{BodyDef, FixtureDef, PolygonShape}
import net.cherryworm.electron.TextureContainer

object Box {
	
	val COULOMBS_CONSTANT = -8.99e9f
	val FORCE_ADJUSTMENT = 1e-9f
	
	val shape = new PolygonShape() {
		setAsBox(0.5f, 0.5f)
	}
	
	def bodyDef(x: Float, y: Float) = new BodyDef {
		`type` = BodyType.StaticBody
		position.set(x, y)
	}
	
}

import net.cherryworm.electron.GameScreen._
import net.cherryworm.electron.game.Box._

class Box(level: Level, x: Float, y: Float, textureId: String, chargeOff: Float, chargeOn: Float, frictionParam: Float, restitutionParam: Float) extends Entity(
	level,
	bodyDef(x, y),
	new FixtureDef {
		density = 1
		friction = frictionParam
		restitution = restitutionParam
		shape = Box.shape
	},
	TextureContainer(textureId),
	Option(new PointLight(level.rayHandler, LIGHT_RAYS, Color.BLACK, 0f, 0f, 0f) {
		setActive(false)
		setXray(true)
	})
) {
	
	def this(level: Level, x: Int, y: Int, scanner: Scanner) {
		this(level, x + 0.5f, y + 0.5f, scanner.next(), scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat())
	}
	
	override def update(delta: Float, stateOn: Boolean): Unit = {
		val charge = if (stateOn) chargeOn else chargeOff
		
		if (isCharged) for (light <- light) {
			light.setColor(charge match {
				case 0 => Color.BLACK
				case i if i > 0 => new Color(0X00642BFF)
				case i if i < 0 => new Color(0xBF3B00FF)
			})
			light.setActive(charge != 0)
			light.setDistance(charge.abs * 2)
		}
		
		for (player <- level.players) {
			val thisCenter = body.getPosition.cpy() add body.getMassData.center
			val playerCenter = player.body.getPosition.cpy() add player.body.getMassData.center
			
			val playerToThis = thisCenter.cpy() sub playerCenter
			
			val f = (COULOMBS_CONSTANT * FORCE_ADJUSTMENT * charge * player.charge) / playerToThis.len2()
			
			player.body.applyForceToCenter(playerToThis.cpy().nor scl f, true)
		}
	}
	
	override def render(batch: SpriteBatch, stateOn: Boolean): Unit = {
		super.render(batch, stateOn)
		
		if (isCharged) {
			val charge = if (stateOn) chargeOn else chargeOff
			
			batch.draw(TextureContainer(charge match {
				case 0 => "neutral_charge"
				case i if i > 0 => "positive_charge"
				case i if i < 0 => "negative_charge"
			}), x - 0.25f, y - 0.25f, 0.5f, 0.25f, 0.25f, 0.5f, 1, 1, body.getAngle * MathUtils.radiansToDegrees)
		}
	}
	
	def isCharged = chargeOff != 0 || chargeOn != 0
	
}
