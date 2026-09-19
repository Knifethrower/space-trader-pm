package com.brucelet.spacetrader.platform;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Properties-file backed replacement for android.content.SharedPreferences, keeping the
 * same call shapes (getInt/getString/getBoolean/contains and edit().putX().commit()).
 */
public class SharedPreferences {

	private final Path file;
	private final Map<String, String> values = new TreeMap<>();

	public SharedPreferences(Path file) {
		this.file = file;
		if (file != null && Files.exists(file)) {
			Properties p = new Properties();
			try (InputStream in = Files.newInputStream(file)) {
				p.load(in);
			} catch (IOException e) {
				throw new IllegalStateException("Cannot read " + file, e);
			}
			for (String k : p.stringPropertyNames()) values.put(k, p.getProperty(k));
		}
	}

	public boolean contains(String key) { return values.containsKey(key); }

	public int getInt(String key, int def) {
		String v = values.get(key);
		return v == null ? def : Integer.parseInt(v);
	}

	public boolean getBoolean(String key, boolean def) {
		String v = values.get(key);
		return v == null ? def : Boolean.parseBoolean(v);
	}

	public String getString(String key, String def) {
		String v = values.get(key);
		return v == null ? def : v;
	}

	public Editor edit() { return new Editor(); }

	public class Editor {
		private final Map<String, String> pending = new TreeMap<>();
		private boolean clear;

		public Editor putInt(String k, int v) { pending.put(k, Integer.toString(v)); return this; }
		public Editor putBoolean(String k, boolean v) { pending.put(k, Boolean.toString(v)); return this; }
		public Editor putString(String k, String v) { pending.put(k, v); return this; }
		public Editor clear() { clear = true; return this; }

		public boolean commit() {
			if (clear) values.clear();
			values.putAll(pending);
			pending.clear();
			clear = false;
			if (file == null) return true;
			Properties p = new Properties();
			p.putAll(values);
			try {
				Path dir = file.toAbsolutePath().getParent();
				Files.createDirectories(dir);
				Path tmp = dir.resolve(file.getFileName() + ".tmp");
				try (OutputStream out = Files.newOutputStream(tmp)) {
					p.store(out, "Space Trader save");
					out.flush();
				}
				try (FileChannel ch = FileChannel.open(tmp, StandardOpenOption.WRITE)) {
					ch.force(true); // make sure the bytes are on the card before the rename
				}
				try {
					Files.move(tmp, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
				} catch (AtomicMoveNotSupportedException e) {
					Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING); // e.g. some FAT/exFAT mounts
				}
				return true;
			} catch (IOException e) {
				System.err.println("[save] write failed for " + file + ": " + e);
				return false;
			}
		}

		public void apply() { commit(); }
	}
}
