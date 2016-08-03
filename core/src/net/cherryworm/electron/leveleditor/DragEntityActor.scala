package net.cherryworm.electron.leveleditor

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import net.cherryworm.electron.game.EntityInfo


case class DragEntityActor(info: Either[EntitySpec, EntityInfo]) {
	var pos = new Vector2

	def render(batch: SpriteBatch): Unit = {
		batch.begin()
		val region = info match {
			case Left(s) => s.region
			case Right(i) => i.texture
		}
		batch.draw(region, pos.x - 0.5f, pos.y - 0.5f, 0.5f, 0.5f, 1, 1, 1, 1, 0f)
		batch.end()
	}
}
