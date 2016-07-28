package net.cherryworm.electron.desktop

import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}
import com.github.czyzby.kiwi.util.gdx.GdxUtilities
import net.cherryworm.electron.Electron

object DesktopLauncher extends App {
	
	new LwjglApplication(new Electron, new LwjglApplicationConfiguration {
		fullscreen = false
		resizable = true
		width = 800
		height = 450
		useGL30 = System.getProperty("os.name").contains("Windows")
	})
	
}
