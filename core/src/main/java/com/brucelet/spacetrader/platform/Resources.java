package com.brucelet.spacetrader.platform;

import com.brucelet.spacetrader.R;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Minimal stand-in for android.content.res.Resources. Loads the original Android
 * strings/arrays XML at runtime and resolves the ids generated into {@link R}.
 */
public class Resources {

	private static final Resources INSTANCE = new Resources();

	public static Resources get() { return INSTANCE; }

	private final Map<Integer, String> idToName = new HashMap<>();
	private final Map<String, String> strings = new HashMap<>();
	private final Map<String, Map<String, String>> plurals = new HashMap<>();
	private final Map<String, String[]> arrays = new HashMap<>();

	private Resources() {
		for (Class<?> c : R.class.getDeclaredClasses()) {
			for (Field f : c.getFields()) {
				if (Modifier.isStatic(f.getModifiers()) && f.getType() == int.class) {
					try {
						idToName.put(f.getInt(null), f.getName());
					} catch (IllegalAccessException e) {
						throw new IllegalStateException(e);
					}
				}
			}
		}
		try {
			load("res/strings.xml");
			load("res/arrays.xml");
			// Resolve @string/name references (used by the system-name and similar arrays).
			for (Map.Entry<String, String> e : strings.entrySet()) e.setValue(resolve(e.getValue()));
			for (String[] a : arrays.values())
				for (int i = 0; i < a.length; i++) a[i] = resolve(a[i]);
		} catch (Exception e) {
			throw new IllegalStateException("Failed to load resources", e);
		}
	}

	private void load(String path) throws Exception {
		try (InputStream in = Resources.class.getClassLoader().getResourceAsStream(path)) {
			NodeList nodes = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(in).getDocumentElement().getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				Node n = nodes.item(i);
				if (!(n instanceof Element)) continue;
				Element e = (Element) n;
				String name = e.getAttribute("name");
				switch (e.getTagName()) {
					case "string":
						strings.put(name, unescape(e.getTextContent()));
						break;
					case "plurals": {
						Map<String, String> m = new HashMap<>();
						for (Element item : items(e)) m.put(item.getAttribute("quantity"), unescape(item.getTextContent()));
						plurals.put(name, m);
						break;
					}
					case "string-array": case "array": case "integer-array": {
						List<String> l = new ArrayList<>();
						for (Element item : items(e)) l.add(unescape(item.getTextContent()));
						arrays.put(name, l.toArray(new String[0]));
						break;
					}
					default:
				}
			}
		}
	}

	private String resolve(String v) {
		if (v.startsWith("@string/")) {
			String target = strings.get(v.substring("@string/".length()));
			if (target == null) throw new IllegalStateException("Unresolved reference " + v);
			return target;
		}
		return v;
	}

	private static List<Element> items(Element parent) {
		List<Element> out = new ArrayList<>();
		NodeList nl = parent.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++)
			if (nl.item(i) instanceof Element) out.add((Element) nl.item(i));
		return out;
	}

	/** Android string-resource escaping: strips surrounding quotes and handles backslash escapes (n, t, u+hex, and quoted literals). */
	private static String unescape(String s) {
		s = s.trim();
		if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) s = s.substring(1, s.length() - 1);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '\\' && i + 1 < s.length()) {
				char d = s.charAt(++i);
				switch (d) {
					case 'n': sb.append('\n'); break;
					case 't': sb.append('\t'); break;
					case 'u':
						sb.append((char) Integer.parseInt(s.substring(i + 1, i + 5), 16));
						i += 4;
						break;
					default: sb.append(d);
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	private String name(int id) {
		String n = idToName.get(id);
		if (n == null) throw new IllegalArgumentException("Unknown resource id 0x" + Integer.toHexString(id));
		return n;
	}

	public String getString(int id) {
		String s = strings.get(name(id));
		if (s == null) throw new IllegalArgumentException("No string " + name(id));
		return s;
	}

	public String getString(int id, Object... args) {
		return String.format(getString(id), args);
	}

	public String getQuantityString(int id, int quantity, Object... args) {
		Map<String, String> m = plurals.get(name(id));
		if (m == null) throw new IllegalArgumentException("No plurals " + name(id));
		String s = m.get(quantity == 1 ? "one" : "other");
		if (s == null) s = m.get("other");
		return String.format(s, args);
	}

	public String[] getStringArray(int id) {
		String[] a = arrays.get(name(id));
		if (a == null) throw new IllegalArgumentException("No array " + name(id));
		return a.clone();
	}

	public int[] getIntArray(int id) {
		String[] a = getStringArray(id);
		int[] out = new int[a.length];
		for (int i = 0; i < a.length; i++) out[i] = Integer.parseInt(a[i]);
		return out;
	}
}
