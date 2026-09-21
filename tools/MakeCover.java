import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Makes the PortMaster cover from the game's own title art: a 4:3 crop of the 16:9 image, scaled to 640x480.
 * (The art is photo-like, so PNG files get large quickly; 640x480 keeps the cover around half a megabyte.)
 * Usage: java tools/MakeCover.java core/src/main/resources/sprites/title_bg.jpg portmaster/spacetrader/cover.png
 */
public class MakeCover {
	public static void main(String[] a) throws Exception {
		BufferedImage in = ImageIO.read(new File(a[0]));
		int h = in.getHeight(), w = h * 4 / 3;
		int x = (in.getWidth() - w) / 2; // the planets, the logo and the ship all sit in the middle
		BufferedImage out = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = out.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.drawImage(in.getSubimage(x, 0, w, h), 0, 0, 640, 480, null);
		g.dispose();
		ImageIO.write(out, "png", new File(a[1]));
		System.out.println(a[1] + " 640x480, " + new File(a[1]).length() / 1024 + " KB");
	}
}
