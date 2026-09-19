import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

/** Usage: java tools/ResizeImage.java <in> <out.jpg> <width> <height> [quality 0-1]
 *  Scales an image (bicubic) and writes it as a JPEG, so large artwork stays small and fast to decode. */
public class ResizeImage {
	public static void main(String[] a) throws Exception {
		BufferedImage src = ImageIO.read(new File(a[0]));
		int w = Integer.parseInt(a[2]), h = Integer.parseInt(a[3]);
		float q = a.length > 4 ? Float.parseFloat(a[4]) : 0.88f;
		BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = out.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.drawImage(src, 0, 0, w, h, null);
		g.dispose();
		ImageWriter wr = ImageIO.getImageWritersByFormatName("jpg").next();
		ImageWriteParam p = wr.getDefaultWriteParam();
		p.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		p.setCompressionQuality(q);
		File f = new File(a[1]);
		f.delete();
		try (FileImageOutputStream os = new FileImageOutputStream(f)) {
			wr.setOutput(os);
			wr.write(null, new IIOImage(out, null, null), p);
		}
		wr.dispose();
		System.out.println(a[1] + ": " + w + "x" + h + ", " + f.length() / 1024 + " KB");
	}
}
