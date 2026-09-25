package ibxm;

import java.io.File;
import java.io.FileInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

/**
 * Manual desktop proof that the vendored IBXM engine (ibxm/*.java, from martincameron/micromod,
 * BSD 3-Clause, see ibxm/LICENSE-ibxm.txt) actually decodes and plays a tracker module.
 *
 * Not part of the shipped game: it plays straight through javax.sound.sampled, with no libGDX or
 * OpenAL involved, so it proves the library itself works before we wire it into the real app.
 *
 * Usage: java ibxm.IbxmPlaybackTest path/to/song.mod [secondsToPlay]
 */
public class IbxmPlaybackTest {
	static final int SAMPLE_RATE = 44100;

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println("Usage: java ibxm.IbxmPlaybackTest path/to/song.mod [secondsToPlay]");
			return;
		}
		int playSeconds = args.length > 1 ? Integer.parseInt(args[1]) : 20;

		byte[] data;
		try (FileInputStream in = new FileInputStream(new File(args[0]))) {
			data = in.readAllBytes();
		}
		Module module = new Module(data);
		System.out.println("Loaded \"" + module.songName.trim() + "\", " + module.numChannels + " channels, "
				+ module.numInstruments + " instruments, " + module.numPatterns + " patterns.");

		IBXM player = new IBXM(module, SAMPLE_RATE);
		int duration = player.calculateSongDuration();
		System.out.println("Full song duration: " + (duration / SAMPLE_RATE) + " s. Playing " + playSeconds + " s.");

		AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 2, true, true); // 16-bit, stereo, signed, big-endian
		SourceDataLine line = AudioSystem.getSourceDataLine(format);
		line.open(format);
		line.start();
		System.out.println("Opened audio line: " + line.getFormat());

		int[] mixBuf = new int[player.getMixBufferLength()];
		byte[] outBuf = new byte[mixBuf.length * 4]; // 2 bytes/sample * 2 channels
		long samplesWritten = 0, target = (long) playSeconds * SAMPLE_RATE;
		long sumAbs = 0, peak = 0;

		while (samplesWritten < target) {
			int count = player.getAudio(mixBuf); // "count" stereo sample pairs
			int outIdx = 0;
			for (int i = 0; i < count * 2; i++) {
				int ampl = mixBuf[i];
				sumAbs += Math.abs(ampl);
				if (Math.abs(ampl) > peak) peak = Math.abs(ampl);
				outBuf[outIdx++] = (byte) (ampl >> 8);
				outBuf[outIdx++] = (byte) ampl;
			}
			line.write(outBuf, 0, outIdx);
			samplesWritten += count;
		}
		line.drain();
		line.stop();
		line.close();

		long totalAmpls = samplesWritten * 2;
		System.out.println("Wrote " + samplesWritten + " stereo samples ("
				+ (samplesWritten / SAMPLE_RATE) + " s). Mean |amplitude|: "
				+ (sumAbs / Math.max(1, totalAmpls)) + ", peak: " + peak
				+ " (of a 16-bit range up to 32767) -- non-zero and varying means real audio was rendered.");
	}
}
