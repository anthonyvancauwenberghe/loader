package loader.client;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.net.MalformedURLException;
import java.net.URL;

public class Stub implements AppletStub {
	@Override
	public void appletResize(int width, int height) {
		System.out.println("New Size: '" + width + ", " + height + "'");
	}

	@Override
	public AppletContext getAppletContext() {
		return null;
	}

	@Override
	public URL getCodeBase() {
		try {
			return new URL("http://deviouspk.com/");
		} catch (MalformedURLException ignore) {
		}
		return null;
	}

	@Override
	public URL getDocumentBase() {
		return getCodeBase();
	}

	@Override
	public String getParameter(String name) {
		return Client.PARAMETERS.get(name);
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
