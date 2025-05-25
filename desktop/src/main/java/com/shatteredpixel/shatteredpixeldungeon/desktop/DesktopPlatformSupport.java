/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.watabou.input.ControllerHandler;
import com.watabou.noosa.Game;
import com.watabou.plugins.PluginManifest;
import com.watabou.utils.PlatformSupport;
import com.watabou.utils.Point;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

public class DesktopPlatformSupport extends PlatformSupport {

	//we recall previous window sizes as a workaround to not save maximized size to settings
	//have to do this as updateDisplaySize is called before maximized is set =S
	protected static Point[] previousSizes = null;

	@Override
	public void updateDisplaySize() {
		if (previousSizes == null) {
			previousSizes = new Point[2];
			previousSizes[1] = SPDSettings.windowResolution();
		} else {
			previousSizes[1] = previousSizes[0];
		}
		previousSizes[0] = new Point(Game.width, Game.height);
		if (!SPDSettings.fullscreen()) {
			SPDSettings.windowResolution(previousSizes[0]);
		}
	}

	private static boolean first = true;

	@Override
	public void updateSystemUI() {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				if (SPDSettings.fullscreen()) {
					int monitorNum = 0;
					if (!first) {
						Graphics.Monitor[] monitors = Gdx.graphics.getMonitors();
						for (int i = 0; i < monitors.length; i++) {
							if (((Lwjgl3Graphics.Lwjgl3Monitor) Gdx.graphics.getMonitor()).getMonitorHandle()
									== ((Lwjgl3Graphics.Lwjgl3Monitor) monitors[i]).getMonitorHandle()) {
								monitorNum = i;
							}
						}
					} else {
						monitorNum = SPDSettings.fulLScreenMonitor();
					}

					Graphics.Monitor[] monitors = Gdx.graphics.getMonitors();
					if (monitors.length <= monitorNum) {
						monitorNum = 0;
					}
					Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode(monitors[monitorNum]));
					SPDSettings.fulLScreenMonitor(monitorNum);
				} else {
					Point p = SPDSettings.windowResolution();
					Gdx.graphics.setWindowedMode(p.x, p.y);
				}
				first = false;
			}
		});
	}

	@Override
	public boolean connectedToUnmeteredNetwork() {
		return true; //no easy way to check this in desktop, just assume user doesn't care
	}

	@Override
	public boolean supportsVibration() {
		//only supports vibration via controller
		return ControllerHandler.vibrationSupported();
	}

	/* FONT SUPPORT */

	//custom pixel font, for use with Latin and Cyrillic languages
	private static FreeTypeFontGenerator basicFontGenerator;
	//droid sans fallback, for asian fonts
	private static FreeTypeFontGenerator asianFontGenerator;

	@Override
	public void setupFontGenerators(int pageSize, boolean systemfont) {
		//don't bother doing anything if nothing has changed
		if (fonts != null && this.pageSize == pageSize && this.systemfont == systemfont) {
			return;
		}
		this.pageSize = pageSize;
		this.systemfont = systemfont;

		resetGenerators(false);
		fonts = new HashMap<>();

		if (systemfont) {
			basicFontGenerator = asianFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/droid_sans.ttf"));
		} else {
			basicFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pixel_font.ttf"));
			asianFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/droid_sans.ttf"));
		}

		fonts.put(basicFontGenerator, new HashMap<>());
		fonts.put(asianFontGenerator, new HashMap<>());

		packer = new PixmapPacker(pageSize, pageSize, Pixmap.Format.RGBA8888, 1, false);
	}

	private static Matcher asianMatcher = Pattern.compile("\\p{InHangul_Syllables}|" +
			"\\p{InCJK_Unified_Ideographs}|\\p{InCJK_Symbols_and_Punctuation}|\\p{InHalfwidth_and_Fullwidth_Forms}|" +
			"\\p{InHiragana}|\\p{InKatakana}").matcher("");

	@Override
	protected FreeTypeFontGenerator getGeneratorForString(String input) {
		if (asianMatcher.reset(input).find()) {
			return asianFontGenerator;
		} else {
			return basicFontGenerator;
		}
	}

	//splits on newline (for layout), chinese/japanese (for font choice), and '_'/'**' (for highlighting)
	private Pattern regularsplitter = Pattern.compile(
			"(?<=\n)|(?=\n)|(?<=_)|(?=_)|(?<=\\*\\*)|(?=\\*\\*)|" +
					"(?<=\\p{InHiragana})|(?=\\p{InHiragana})|" +
					"(?<=\\p{InKatakana})|(?=\\p{InKatakana})|" +
					"(?<=\\p{InCJK_Unified_Ideographs})|(?=\\p{InCJK_Unified_Ideographs})|" +
					"(?<=\\p{InCJK_Symbols_and_Punctuation})|(?=\\p{InCJK_Symbols_and_Punctuation})");

	//additionally splits on spaces, so that each word can be laid out individually
	private Pattern regularsplitterMultiline = Pattern.compile(
			"(?<= )|(?= )|(?<=\n)|(?=\n)|(?<=_)|(?=_)|(?<=\\*\\*)|(?=\\*\\*)|" +
					"(?<=\\p{InHiragana})|(?=\\p{InHiragana})|" +
					"(?<=\\p{InKatakana})|(?=\\p{InKatakana})|" +
					"(?<=\\p{InCJK_Unified_Ideographs})|(?=\\p{InCJK_Unified_Ideographs})|" +
					"(?<=\\p{InCJK_Symbols_and_Punctuation})|(?=\\p{InCJK_Symbols_and_Punctuation})");

	@Override
	public String[] splitforTextBlock(String text, boolean multiline) {
		if (multiline) {
			return regularsplitterMultiline.split(text);
		} else {
			return regularsplitter.split(text);
		}
	}

	@Override
	public List<PluginManifest> loadPlugins() {
		List<PluginManifest> manifests = new ArrayList<>();
		if (Files.isDirectory(Paths.get("plugins/"))) {
			File[] files;
			try {
				files = new File("plugins/").listFiles();
				if (files == null) {
					return manifests;
				}
				for (File file : files) {
					if (file.getName().endsWith(".jar")) {
						JarFile jar = new JarFile(file.toPath().toAbsolutePath().toFile());
						ZipEntry manifest = jar.getEntry("plugin_manifest.txt");
						if (manifest != null) {
							InputStream input = jar.getInputStream(manifest);
							ByteArrayOutputStream result = new ByteArrayOutputStream();
							//might change buffer, 2kb should be fine. Can a manifest even be that big?
							byte[] buffer = new byte[2048];
							for (int length; (length = input.read(buffer)) != -1; ) {
								result.write(buffer, 0, length);
							}
							Gdx.app.log("PluginLoader", "Found manifest in: " + file.toPath());
							manifests.add(new PluginManifest(result.toString(), file.toPath().toAbsolutePath().toUri().toString()));
						} else {
							Gdx.app.error("PluginLoader", "Failed to find manifest in: " + file.getName());
						}
						jar.close();
					}
				}
			} catch (IOException e) {
				Gdx.app.error("PluginLoader", e.toString());
			}
		} else {
			try {
				Files.createDirectories(Paths.get("plugins"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return manifests;

	}

	JmDNS dns;
	@Override
	public void registerService(int port) {
		if(dns ==null)
		{
            try {
				InetAddress bindAddress = null;
				Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
				while (interfaces.hasMoreElements()) {
					NetworkInterface iface = interfaces.nextElement();
					if (!iface.isLoopback() && iface.isUp()) {
						Enumeration<InetAddress> addresses = iface.getInetAddresses();
						while (addresses.hasMoreElements()) {
							InetAddress addr = addresses.nextElement();
							if (addr instanceof Inet4Address) { // Check if it's an IPv4 address
								bindAddress = addr;
								break; // Found the first non-loopback, up IPv4 address
							}
						}
						if (bindAddress != null) {
							break; // Exit the outer loop once an IPv4 address is found
						}
					}
				}
				if (bindAddress != null) {
					dns = JmDNS.create(bindAddress);
				} else {
					dns = JmDNS.create();
				}
				ServiceInfo serviceInfo = ServiceInfo.create("._mppd._tcp.local.", SPDSettings.serverName(), port, "");
				dns.registerService(serviceInfo);
				System.out.println(serviceInfo.getHostAddresses()[0]);
				System.out.println("Service registered: " + serviceInfo.getName() + " on port " + serviceInfo.getPort());
				System.out.println("Service type: " + serviceInfo.getType());
			} catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

	}
	@Override
	public void unregisterService() {
		dns.unregisterAllServices();
		System.out.println("Service unregistered");
        try {
            dns.close();
        } catch (IOException e) {
			e.printStackTrace();
        }
        dns = null;
	}
}
