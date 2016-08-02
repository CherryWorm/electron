package net.cherryworm.electron.leveleditor

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import net.cherryworm.electron.TextureContainer
import net.cherryworm.electron.game._

trait EntitySpec {
	val textureID: String
	def mkNew(pos: Vector2): EntityInfo
	lazy val region = TextureContainer.getTexture(this.textureID)
}

class Sidebar(camera: Camera) {
	val entities: List[EntitySpec] = PlayerSpec :: ExitSpec :: BoxSpec :: Nil

	// entity box size
	val bSize = 0.9f

	def inside(p: Vector2): Boolean = {
		camera.viewportWidth > p.x && p.x > camera.viewportWidth - 1f
	}

	def entityAt(p: Vector2): Option[EntitySpec] = {
		boxes() find {case (ePos, spec) =>
			p.x >= ePos.x && p.y >= ePos.y &&
			p.x <= ePos.x + bSize && p.y <= ePos.y + bSize
		} map (_._2)
	}

	def boxes(): List[(Vector2, EntitySpec)] = {
		entities.zipWithIndex map {case (e: EntitySpec, i: Int) =>
			val x = camera.viewportWidth - 1f
			val y = camera.viewportHeight - 1f - (1f - bSize) / 2f - i
			(new Vector2(x, y), e)
		}
	}

	def render(spriteBatch: SpriteBatch, shapeRenderer: ShapeRenderer): Unit = {
		shapeRenderer.begin()
		shapeRenderer.rect(camera.viewportWidth - 1.1f, 0f, 1.1f, camera.viewportHeight)
		shapeRenderer.end()

		spriteBatch.begin()
		for ((pos: Vector2, spec: EntitySpec) <- boxes()) {
			spriteBatch.draw(spec.region, pos.x, pos.y, 0.5f, 0.5f, bSize, bSize, 1, 1, 0f)
		}
		spriteBatch.end()
	}
}
