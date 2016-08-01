package net.cherryworm.electron.leveleditor

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2


class DragEntityActor(spec: EntitySpec) {
	var x = 0f
	var y = 0f

	def render(batch: SpriteBatch): Unit = {
		batch.begin()
		batch.draw(spec.region, x, y, 0.5f, 0.5f, 1, 1, 1, 1, 0f)
		batch.end()
	}
}
