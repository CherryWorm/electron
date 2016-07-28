package net.cherryworm.electron.game

import box2dLight.Light
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, TextureRegion}
import com.badlogic.gdx.math.{MathUtils, Vector2}
import com.badlogic.gdx.physics.box2d.{BodyDef, CircleShape, FixtureDef, PolygonShape}

abstract class Entity(level: Level, bodyDef: BodyDef, fixtureDef: FixtureDef, protected var textureRegion: TextureRegion, val light: Option[Light] = Option.empty) {
	
	val body = level.world.createBody(bodyDef)
	body.setUserData(this)
	val fixture = body.createFixture(fixtureDef)
	fixture.setUserData(this)
	
	def render(batch: SpriteBatch, stateOn: Boolean): Unit = {
		val x = body.getPosition.x
		val y = body.getPosition.y
		
		if (textureRegion != null) fixtureDef.shape match {
			case _: PolygonShape =>
				batch.draw(textureRegion, x - 0.5f, y - 0.5f, 1f / 2, 1f / 2, 1, 1, 1, 1, body.getAngle * MathUtils.radiansToDegrees)
			case _: CircleShape => {
				val trans = new Vector2(1, 1) scl 0.5f
				trans.rotateRad(body.getAngle)
				batch.draw(textureRegion, x - 0.5f + trans.x, y - 0.5f + trans.y, 0.5f, 0.5f, 1, 1, 1, 1, body.getAngle * MathUtils.radiansToDegrees)
			}
		}
		for (light <- light) fixtureDef.shape match {
			case _: PolygonShape =>
				light.setPosition(x, y)
			case _: CircleShape => {
				val trans = new Vector2(1, 1) scl 0.5f
				trans.rotateRad(body.getAngle)
				light.setPosition(x + trans.x, y + trans.y)
			}
		}
	}
	
	def update(delta: Float, stateOn: Boolean): Unit
	
}