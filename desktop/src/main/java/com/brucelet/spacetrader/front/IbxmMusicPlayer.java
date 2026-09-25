package com.brucelet.spacetrader.front;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import ibxm.Channel;
import ibxm.IBXM;
import ibxm.Module;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Development-only proof that the vendored IBXM tracker engine (ibxm/*.java, see
 * ibxm/LICENSE-ibxm.txt) can drive libGDX's audio output. Loads a MOD/S3M/XM file and plays it
 * on a background thread through a libGDX AudioDevice, looping the module once it ends.
 *
 * Not part of the shipped game yet: only started when the "st.testmusic" system property is set
 * (see DesktopLauncher and SpaceTraderApp.create()), which is also what re-enables audio at all.
 */
public class IbxmMusicPlayer {
	static final int SAMPLE_RATE = 44100;

	Thread thread;
	volatile boolean running;
	AudioDevice device;

	public void start(String modulePath) {
		Module module;
		try {
			module = new Module(Files.readAllBytes(Paths.get(modulePath)));
		} catch (Exception e) {
			System.out.println("[music] could not load " + modulePath + ": " + e);
			return;
		}
		System.out.println("[music] playing \"" + module.songName.trim() + "\" from " + modulePath);
		device = Gdx.audio.newAudioDevice(SAMPLE_RATE, false); // stereo
		running = true;
		thread = new Thread(() -> playLoop(module), "ibxm-music");
		thread.setDaemon(true);
		thread.setPriority(Thread.MAX_PRIORITY); // audio decoding must not be starved by the render thread on weak CPUs
		thread.start();
	}

	void playLoop(Module module) {
		while (running) {
			IBXM player = new IBXM(module, SAMPLE_RATE);
			player.setInterpolation(Channel.NEAREST); // cheapest resampling; cuts CPU cost on weak devices
			int total = player.calculateSongDuration();
			int[] mixBuf = new int[player.getMixBufferLength()];
			short[] outBuf = new short[mixBuf.length * 2];
			int written = 0;
			while (running && written < total) {
				int count = player.getAudio(mixBuf);
				for (int i = 0; i < count * 2; i++) outBuf[i] = (short) mixBuf[i];
				device.writeSamples(outBuf, 0, count * 2);
				written += count;
			}
			// loop back to the start of the module for continuous background music
		}
	}

	public void stop() {
		running = false;
		if (thread != null) {
			try { thread.join(500); } catch (InterruptedException ignored) {}
		}
		if (device != null) device.dispose();
	}
}
