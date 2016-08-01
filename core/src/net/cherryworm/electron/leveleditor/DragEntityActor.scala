package net.cherryworm.electron.leveleditor

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2


class DragEntityActor(_spec: EntitySpec) {
	var pos = new Vector2
	val spec = _spec

	def render(batch: SpriteBatch): Unit = {
		batch.begin()
		batch.draw(spec.region, pos.x - 0.5f, pos.y - 0.5f, 0.5f, 0.5f, 1, 1, 1, 1, 0f)
		batch.end()
	}
}
