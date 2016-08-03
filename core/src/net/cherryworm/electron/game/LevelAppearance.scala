package net.cherryworm.electron.game

import java.util.Scanner

import com.badlogic.gdx.graphics.Color

case class LevelAppearance (
	positiveCharge: Appearance,
	neutralCharge:  Appearance,
	negativeCharge: Appearance,
	positivePlayer: Appearance,
	negativePlayer: Appearance,
	exit: Appearance,
	ambientLightColor: Color
)

object LevelAppearance {
	val DEFAULT = LevelAppearance(
		positiveCharge = Appearance(Color.GREEN, 1f, "positive_charge"),
		neutralCharge  = Appearance(Color.WHITE, 1f, "neutral_charge"),
		negativeCharge = Appearance(Color.RED, 1f, "negative_charge"),
		positivePlayer = Appearance(Color.GREEN, 1f, "player"),
		negativePlayer = Appearance(Color.RED, 1f, "player"),
		exit           = Appearance(Color.WHITE, 1f, "exit"),
		ambientLightColor = new Color(0.35f, 0.35f, 0.35f, 0.35f)
	)

	def read(scanner: Scanner): LevelAppearance = {
		def readColor() = new Color(scanner.nextFloat, scanner.nextFloat, scanner.nextFloat, scanner.nextFloat)
		def readLightStrength() = scanner.nextFloat
		def readTexture() = scanner.next
		def readAppearance() = Appearance(readColor(), readLightStrength(), readTexture())

		LevelAppearance (
			ambientLightColor = readColor(),
			exit           = readAppearance(),
			positiveCharge = readAppearance(),
			neutralCharge  = readAppearance(),
			negativeCharge = readAppearance(),
			positivePlayer = readAppearance(),
			negativePlayer = readAppearance()
		)
	}
}

