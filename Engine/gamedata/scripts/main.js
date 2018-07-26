var AL = Java.type("org.lwjgl.openal.AL");
var SoundSystem = Java.type("alex9932.utils.sound.SoundSystem");

function levelLoaded(sLevelName) {
	print("Loading level: " + sLevelName);
}

function construct() {
	script.get("main.js").addOnloadEvent("levelLoaded");
	script.loadLevel("eng_test");
	AL.setCurrentThread(SoundSystem.getCaps());
	//sound.getNewSource(0, 0, 0, 1, "gamedata/sounds/ambient/forest.ogg").play();
	//sound.getNewSource(0, 0, 0, 1, "gamedata/sounds/ambient/wind.ogg").play();
}

function destruct() {
	
}

function main() {
	
}

function render() {
	
}