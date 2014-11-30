package loader.client;

import java.awt.Color;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * Created by Cory
 * on 11/08/2014.
 */
public class Client {

    public static final String JAR_NAME = "Webclient.jar";
    public static final String JAR_URL = "http://cache.arteropk.com/" + JAR_NAME;
    public static final String CLIENT_CLASS = "dvpk.DeviousPK";

    private static boolean isInitialized;

    public static void init() {
        if(isInitialized)
            return;
        try {
            downloadJar();
            isInitialized = true;
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    public static boolean isIsInitialized(){
        return isInitialized;
    }

    public static void downloadJar() {
        try {
            File file = new File(getPublicCache() + "Client" + File.separator + JAR_NAME);
            try {
                file.getParentFile().mkdirs();
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
            BufferedInputStream input = new BufferedInputStream(new URL(JAR_URL).openStream());
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
            int bytesRead = 0;
            while ((bytesRead = input.read()) != -1) {
                output.write(bytesRead);
            }
            input.close();
            output.close();
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    public static URLClassLoader getClassLoader() {
        try {
            URLClassLoader classLoader = new URLClassLoader(new URL[]{new File(getPublicCache() + "Client" + File.separator + JAR_NAME).toURI().toURL()});
            classLoader.loadClass(CLIENT_CLASS);
            return classLoader;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPublicCache() {
        String home = System.getProperty("user.home");
        String path = home + File.separator + getCacheName() + File.separator;
        new File(path).mkdirs();
        return path;
    }

    public static String getCacheName() {
        return "DeviousPKCache";
    }

	public static java.applet.Applet getApplet() {
		try {
			URLClassLoader classLoader = getClassLoader();
			if (classLoader != null) {
				Class<?> client = classLoader.loadClass(CLIENT_CLASS);
				java.applet.Applet applet = (java.applet.Applet) client.newInstance();
				applet.setBackground(Color.BLACK);
				applet.setStub(new Stub());
				return applet;
			}
		} catch (Exception ignore) {
			ignore.printStackTrace();
		}
		return null;
	}


}
