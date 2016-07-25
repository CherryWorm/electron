package net.cherryworm.electron.game

import box2dLight.Light
import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch, TextureRegion}
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.{BodyDef, FixtureDef, World}
import net.cherryworm.electron.GameScreen._

abstract class Entity(world: World, bodyDef: BodyDef, fixtureDef: FixtureDef, textureRegion: TextureRegion, light: Option[Light] = Option.empty) {
	
	val body = world.createBody(bodyDef)
	body.setUserData(this)
	light foreach (_.attachToBody(body))
	val fixture = body.createFixture(fixtureDef)
	
	lazy val sprite = if(textureRegion == null) null else {
		val (x, y) = worldToPixel(body.getPosition)
		new Sprite(textureRegion, x, y, textureRegion.getRegionWidth, textureRegion.getRegionHeight)
	}
	
	def render(batch: SpriteBatch): Unit = if(textureRegion != null) {
		sprite.setX(body.getPosition.x * PIXELS_PER_UNIT)
		sprite.setY(body.getPosition.y * PIXELS_PER_UNIT)
		sprite.setRotation(MathUtils.radiansToDegrees * body.getAngle)
		println(this + ": " + worldToPixel(body.getPosition))
		sprite.draw(batch)
	}
	
	def update(delta: Float): Unit
	
}