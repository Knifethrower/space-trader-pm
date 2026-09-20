import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Makes the PortMaster cover from the game's own title art: a 4:3 crop of the 16:9 image.
 * Usage: java tools/MakeCover.java core/src/main/resources/sprites/title_bg.jpg portmaster/spacetrader/cover.png
 */
public class MakeCover {
	public static void main(String[] a) throws Exception {
		BufferedImage in = ImageIO.read(new File(a[0]));
		int h = in.getHeight(), w = h * 4 / 3;
		int x = (in.getWidth() - w) / 2; // the planets, the logo and the ship all sit in the middle
		BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		out.getGraphics().drawImage(in.getSubimage(x, 0, w, h), 0, 0, null);
		ImageIO.write(out, "png", new File(a[1]));
		System.out.println(a[1] + " " + w + "x" + h);
	}
}
