package net.cherryworm.electron.game

import box2dLight.Light
import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch, TextureRegion}
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.{BodyDef, FixtureDef, World}

abstract class Entity(world: World, bodyDef: BodyDef, fixtureDef: FixtureDef, textureRegion: TextureRegion, light: Option[Light] = Option.empty) {
	
	val body = world.createBody(bodyDef)
	body.setUserData(this)
	val fixture = body.createFixture(fixtureDef)
	
	lazy val sprite = if(textureRegion == null) null else {
		new Sprite(textureRegion, 0, 0, 1, 1)
	}
	
	def render(batch: SpriteBatch): Unit = {
		val x = body.getPosition.x
		val y = body.getPosition.y
		
		if (textureRegion != null) {
			sprite.setX(x)
			sprite.setY(y)
			sprite.setRotation(MathUtils.radiansToDegrees * body.getAngle)
			sprite.draw(batch)
			batch.draw(textureRegion, x, y, 1, 1)
		}
		
		for (light <- light) light.setPosition(x + 0.5f, y + 0.5f)
	}
	
	def update(delta: Float): Unit
	
}