package net.cherryworm.electron.game

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import net.cherryworm.electron.TextureContainer

case class TextureElement(x: Int, y: Int, textureId: String) {
	
	def render(batch: SpriteBatch): Unit = {
		batch.draw(TextureContainer(textureId), x, y, 0.5f, 0.5f, 1, 1, 1, 1, 0)
	}
	
}
