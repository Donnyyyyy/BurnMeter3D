package ru.donny.burnmeter3d.desktop;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class TexturePackerLauncher {

	public static void main(String[] args) {
		TexturePacker.process("C:/Soft/WORKSPACE/Burn Meter 3D/release/android/assets/data/skins/button",
				"C:/Soft/WORKSPACE/Burn Meter 3D/release/android/assets/data/skins/", "model_view_controller");
	}

}
