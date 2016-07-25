package net.cherryworm.electron.desktop

import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}
import net.cherryworm.electron.Electron

object DesktopLauncher extends App {
	
	new LwjglApplication(new Electron, new LwjglApplicationConfiguration {
		fullscreen = false
		resizable = true
		width = 800
		height = 450
		useGL30 = true
	})
	
}
