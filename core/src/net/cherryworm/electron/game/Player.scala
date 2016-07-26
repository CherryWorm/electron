package net.cherryworm.electron.game

import box2dLight.{PointLight, RayHandler}
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.{Color, Texture}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.{BodyDef, CircleShape, FixtureDef, World}

object Player {
	
	lazy val playerBodyDef = new BodyDef {
		`type` = BodyType.DynamicBody
		position.set(5, 5)
	}
	
	lazy val playerFixtureDef = new FixtureDef {
		density = 1f
		friction = 0.4f
		restitution = 0.9f
		shape = new CircleShape {
			setRadius(0.5f)
			setPosition(new Vector2(0.5f, 0.5f))
		}
	}
	
	lazy val playerTextureRegion = new TextureRegion(new Texture(Gdx.files.internal("textures/player.png")))
	
}

import net.cherryworm.electron.GameScreen.LIGHT_RAYS
import net.cherryworm.electron.game.Player._

class Player(world: World, rayHandler: RayHandler) extends Entity(world,
	playerBodyDef,
	playerFixtureDef,
	playerTextureRegion,
	Option(new PointLight(rayHandler, LIGHT_RAYS, new Color(Color.SCARLET.r, Color.SCARLET.g, Color.SCARLET.b, 0.9f), 3f, 0f, 0f) {
		setXray(true)
	})) {
	
	override def update(delta: Float): Unit = {}
}
