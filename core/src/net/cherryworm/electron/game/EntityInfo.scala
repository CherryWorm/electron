package net.cherryworm.electron.game

import com.badlogic.gdx.graphics.g2d.{SpriteBatch, TextureRegion}
import com.badlogic.gdx.math.Vector2
import net.cherryworm.electron.leveleditor.EntitySpec

abstract class EntityInfo {
	val texture: TextureRegion
	val position: Vector2
	def render(batch: SpriteBatch): Unit = {
		batch.draw(texture, position.x, position.y, 0.5f, 0.5f, 1, 1, 1, 1, 0f)
	}

	def at(pos: Vector2): EntityInfo
}
