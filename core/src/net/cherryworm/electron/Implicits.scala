package net.cherryworm.electron

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.{Event, EventListener, InputEvent}

object Implicits {
	
	implicit def eventFunctionToEventListener(f: (Event) => Boolean) = new EventListener {
		override def handle(event: Event): Boolean = f(event)
	}
	
	implicit def unitFunctionToEventListener(f: (Event) => Unit) = eventFunctionToEventListener((e: Event) => {
		f(e)
		false
	})
	
	implicit def unitFunctionToClickListener(f: (InputEvent, Float, Float) => Unit) = new ClickListener() {
		override def clicked(event: InputEvent, x: Float, y: Float) = f(event, x, y)
	}
	
}
