package net.cherryworm.electron.game

import box2dLight.Light
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, TextureRegion}
import com.badlogic.gdx.physics.box2d.{BodyDef, FixtureDef, World}

abstract class Entity(world: World, bodyDef: BodyDef, fixtureDef: FixtureDef, textureRegion: TextureRegion, light: Option[Light] = Option.empty) {
	
	val body = world.createBody(bodyDef)
	body.setUserData(this)
	val fixture = body.createFixture(fixtureDef)
	
	def render(batch: SpriteBatch): Unit = {
		val x = body.getPosition.x
		val y = body.getPosition.y
		
		if (textureRegion != null) batch.draw(textureRegion, x, y, 1, 1)
		for (light <- light) light.setPosition(x + 0.5f, y + 0.5f)
	}
	
	def update(delta: Float): Unit
	
}