package net.cherryworm.electron.game

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import net.cherryworm.electron.TextureContainer
import net.cherryworm.electron.leveleditor.EntitySpec

object BGSpec extends EntitySpec {
	override val textureID = "cave"
	override def mkNew(pos: Vector2): TextureElementInfo = {
		TextureElementInfo(pos, textureID)
	}
}

case class TextureElementInfo(pos: Vector2, textureID: String) extends EntityInfo {
	override val texture = TextureContainer.getTexture(textureID)
	override val position = pos

	def at(pos: Vector2): TextureElementInfo = {
		TextureElementInfo(pos, textureID)
	}
}

case class TextureElement(x: Int, y: Int, textureId: String) {
	
	def render(batch: SpriteBatch): Unit = {
		batch.draw(TextureContainer(textureId), x, y, 0.5f, 0.5f, 1, 1, 1, 1, 0)
	}
	
}
