package net.cherryworm.electron

import com.badlogic.gdx.scenes.scene2d.{Event, EventListener}

object Implicits {
	
	implicit def eventFunctionToEventListener(f: (Event) => Boolean) = new EventListener {
		override def handle(event: Event): Boolean = f(event)
	}
	
	implicit def unitFunctionToEventListener(f: (Event) => Unit) = eventFunctionToEventListener((e: Event) => {
		f(e)
		false
	})
	
}
