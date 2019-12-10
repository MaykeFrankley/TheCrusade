package model.audio;

import java.util.HashMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class AudioPLayer {
	
	private static HashMap<String, Clip> clips;
	private static int gap;
	private static boolean mute = false;
	
	public static void init() {
		clips = new HashMap<String, Clip>();
		gap = 0;
		
		AudioPLayer.load("/Music/Level2Bg.mp3", "Level2Bg");
		AudioPLayer.load("/Music/Level2BossMusic.mp3", "BossOST");
		AudioPLayer.load("/SFX/ItemTake.mp3", "Item");
		AudioPLayer.load("/SFX/FireTrap.mp3", "Fire");
		AudioPLayer.load("/Music/Level1Bg.mp3", "Level1Bg");
		AudioPLayer.load("/Music/Level1BossMusic.mp3", "BossOST");
		AudioPLayer.load("/SFX/RainSFX.mp3", "Rain");
		AudioPLayer.load("/SFX/ItemTake.mp3", "Item");
		AudioPLayer.load("/SFX/playerJump.mp3", "playerjump");
		AudioPLayer.load("/SFX/Sword.mp3", "attack");
		AudioPLayer.load("/SFX/FireBall.mp3", "FireBall");
		AudioPLayer.load("/SFX/Arrow.mp3", "Bow");
		AudioPLayer.load("/SFX/Slide.mp3", "Slide");
		AudioPLayer.load("/SFX/CrateDestroy.mp3", "Crate");
		AudioPLayer.load("/SFX/playerHit.mp3", "Hit");
	}
	
	public static void load(String s, String n) {
		if(clips.get(n) != null) return;
		Clip clip;
		try {			
			AudioInputStream ais = AudioSystem.getAudioInputStream(AudioPLayer.class.getResourceAsStream(s));
			AudioFormat baseFormat = ais.getFormat();
			AudioFormat decodeFormat = new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED,
				baseFormat.getSampleRate(),
				16,
				baseFormat.getChannels(),
				baseFormat.getChannels() * 2,
				baseFormat.getSampleRate(),
				false
			);
			AudioInputStream dais = AudioSystem.getAudioInputStream(decodeFormat, ais);
			clip = AudioSystem.getClip();
			clip.open(dais);
			clips.put(n, clip);
			
			FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(-30.0f);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void play(String s) {
		play(s, gap);
	}
	
	public static void play(String s, int i) {
		if(mute) return;
		Clip c = clips.get(s);
		if(c == null) return;
		if(c.isRunning()) c.stop();
		c.setFramePosition(i);
		while(!c.isRunning()) c.start();
	}
	
	public static void stop(String s) {
		if(clips.get(s) == null) return;
		if(clips.get(s).isRunning()) clips.get(s).stop();
	}
	
	public static void resume(String s) {
		if(mute) return;
		if(clips.get(s).isRunning()) return;
		clips.get(s).start();
	}
	
	public static void loop(String s) {
		loop(s, gap, gap, clips.get(s).getFrameLength() - 1);
	}
	
	public static void loop(String s, int start, int end) {
		loop(s, gap, start, end);
	}
	
	public static void loop(String s, int frame, int start, int end) {
		stop(s);
		if(mute) return;
		clips.get(s).setLoopPoints(start, end);
		clips.get(s).setFramePosition(frame);
		clips.get(s).loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public static void setPosition(String s, int frame) {
		clips.get(s).setFramePosition(frame);
	}
	
	public static void close(String s) {
		stop(s);
		clips.get(s).close();
	}
	
}
