package net.cherryworm.electron.game

import box2dLight.Light
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, TextureRegion}
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.{BodyDef, FixtureDef}

abstract class Entity(level: Level, bodyDef: BodyDef, fixtureDef: FixtureDef, protected var textureRegion: TextureRegion, light: Option[Light] = Option.empty) {
	
	val body = level.world.createBody(bodyDef)
	body.setUserData(this)
	val fixture = body.createFixture(fixtureDef)
	
	def render(batch: SpriteBatch): Unit = {
		val x = body.getMassData.center.x + body.getPosition.x
		val y = body.getMassData.center.y + body.getPosition.y
		
		if (textureRegion != null) batch.draw(textureRegion, body.getPosition.x, body.getPosition.y, 1f / 2, 1f / 2, 1, 1, 1, 1, body.getAngle * MathUtils.radiansToDegrees);
		for (light <- light) light.setPosition(x, y)
	}
	
	def update(delta: Float, stateOn: Boolean): Unit
	
}