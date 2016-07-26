package net.cherryworm.electron.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d._

object Wall {
	
	def wallBodyDef(x: Int, y: Int) = new BodyDef {
		`type` = BodyType.StaticBody
		position.set(x, y)
	}
	
	lazy val wallFixtureDef = new FixtureDef {
		density = 1f
		friction = 1f
		restitution = 1f
		shape = new PolygonShape() {
			setAsBox(1, 1)
		}
	}
	
	lazy val wallTextureRegion = new TextureRegion(new Texture(Gdx.files.internal("textures/wall.png")))
	
}

import net.cherryworm.electron.game.Wall._

class Wall(world: World, x: Int, y: Int) extends Entity(world,
	wallBodyDef(x, y),
	wallFixtureDef,
	wallTextureRegion) {
	
	override def update(delta: Float): Unit = {}
}
