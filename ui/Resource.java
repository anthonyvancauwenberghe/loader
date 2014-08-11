package loader.ui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;

public class Resource {

	public static URL getResource(String name) {
		try {
			return Resource.class.getResource(name);
		} catch (Exception ignore) {
		}
		return null;
	}

	public static Image get(String imageName) {
		try {
			return ImageIO.read(getResource("img/" + imageName + ".png"));
		} catch (Exception ignore) {
		}
		return null;
	}

	public static ArrayList<Image> getIconImages() {
		ArrayList<Image> icons = new ArrayList<Image>();
		icons.add(get("i16"));
		icons.add(get("i32"));
		icons.add(get("i64"));
		return icons;
	}
}
