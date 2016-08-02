package net.cherryworm.electron.game

import box2dLight.PointLight
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, TextureRegion}
import com.badlogic.gdx.graphics.{Color, Texture}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.{BodyDef, CircleShape, FixtureDef}
import net.cherryworm.electron.TextureContainer
import net.cherryworm.electron.leveleditor.EntitySpec

object Player {
	
	def playerBodyDef(x: Float, y: Float) = new BodyDef {
		`type` = BodyType.DynamicBody
		position.set(x, y)
	}
	
	lazy val playerFixtureDef = new FixtureDef {
		density = 1f
		friction = 0.4f
		restitution = 0f
		shape = new CircleShape {
			setRadius(0.5f)
			setPosition(new Vector2(0.5f, 0.5f))
		}
	}
}

object PlayerSpec extends EntitySpec {
	override val textureID = "player"
	override def mkNew(pos: Vector2): PlayerInfo = ???
}

class PlayerInfo(pos: Vector2, charge: Float) extends EntityInfo {
	override val texture = TextureContainer.getTexture("player")
	override val position = pos
}

import net.cherryworm.electron.GameScreen.LIGHT_RAYS
import net.cherryworm.electron.game.Player._

class Player(level: Level, x: Float, y: Float, val charge: Float, appearance: Appearance) extends Entity(
	level,
	playerBodyDef(x, y),
	playerFixtureDef,
	TextureContainer(appearance.texture),
	Option(new PointLight(level.rayHandler, LIGHT_RAYS, appearance.lightColor, appearance.lightStrength * charge.abs, 0f, 0f) {
		setXray(false)
	})
) {
	
	var evacuated = false
	var firstTimeEvacuated = true
	
	override def update(delta: Float, stateOn: Boolean) = {
		if(evacuated && firstTimeEvacuated) {
			firstTimeEvacuated = false
			body.setAngularVelocity(0)
			body.setLinearVelocity(0, 0)
			body.setActive(false)
		}
	}
	
	override def render(batch: SpriteBatch, stateOn: Boolean): Unit = {
		if (!evacuated) super.render(batch, stateOn)
	}
	
	def reset(): Unit = {
		body.setAngularVelocity(0)
		body.setLinearVelocity(0, 0)
		body.setActive(true)
		evacuated = false
		firstTimeEvacuated = true
	}
	
	def evacuate(): Unit = {
		evacuated = true
		
		if (level.players forall (_.evacuated)) {
			level.gameFinished = true
		}
	}
}