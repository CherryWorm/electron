package net.cherryworm.electron.leveleditor

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import net.cherryworm.electron.game._

trait EntitySpec {
	val texture: String
	def mkNew: Entity
}

class Sidebar(camera: Camera) {
	val entities: List[EntitySpec] = PlayerSpec :: ExitSpec :: BoxSpec :: Nil

	def inside(p: Vector2): Boolean = {
		camera.viewportWidth > p.x && p.x > camera.viewportWidth - 1f
	}

	def entityAt(p: Vector2): Option[EntitySpec] = {
		None
	}

	def boxes(): List[(Vector2, EntitySpec)] = {
		(entities zipWithIndex) map {case (e: EntitySpec, i: Int) => {
			(new Vector2(camera.viewportWidth - 1f, i), e)
		}}
	}

	def render(spriteBatch: SpriteBatch, shapeRenderer: ShapeRenderer): Unit = {
		shapeRenderer.begin()
		shapeRenderer.rect(camera.viewportWidth - 1f, 0f, 1f, camera.viewportHeight)
		shapeRenderer.rect(0f, 0f, camera.viewportWidth, camera.viewportHeight)
		for ((pos: Vector2, entity: Entity) <- boxes()) {
			shapeRenderer.rect(pos.x, pos.y, 1f, 1f)
		}
		shapeRenderer.end()
		spriteBatch.begin()
		spriteBatch.end()
	}
}
