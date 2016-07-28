package net.cherryworm.electron

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion

import scala.collection.immutable.HashMap


object TextureContainer {
	
	var textureMap = HashMap[String, TextureRegion]()
	
	
	def addTexturesRecursively(fileHandle: FileHandle): Unit = fileHandle match {
		case file if file.isDirectory => file.list foreach addTexturesRecursively
		case file => addTexture(file)
	}
	
	def addTexture(fileHandle: FileHandle): Unit = addTexture(new Texture(fileHandle), fileHandle.nameWithoutExtension)
	
	def addTexture(texture: Texture, id: String): Unit = addTexture(new TextureRegion(texture), id)
	
	def addTexture(textureRegion: TextureRegion, id: String): Unit = textureMap = textureMap + (id -> textureRegion)
	
	def getTextureOption(id: String) = textureMap get id
	
	def getTexture(id: String) = textureMap(id)
	
	def apply(id: String) = getTexture(id)
	
	def dispose() = textureMap.values map (_.getTexture) foreach (_.dispose())
	
}
