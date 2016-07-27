package net.cherryworm.electron.game

import box2dLight.PointLight
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.{Color, Texture}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.{BodyDef, CircleShape, FixtureDef}

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
	
	lazy val playerTextureRegion = new TextureRegion(new Texture(Gdx.files.internal("textures/player.png")))
	
}

import net.cherryworm.electron.GameScreen.LIGHT_RAYS
import net.cherryworm.electron.game.Player._

class Player(level: Level, x: Float, y: Float, val charge: Float) extends Entity(level,
	playerBodyDef(x, y),
	playerFixtureDef,
	playerTextureRegion,
	Option(new PointLight(level.rayHandler, LIGHT_RAYS, new Color(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, 0.7f), 3f, 0f, 0f) {
		setXray(true)
	})) {
	
	override def update(delta: Float, stateOn: Boolean): Unit = {}
}
