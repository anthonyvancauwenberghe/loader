package loader.client;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

public class Client {

	public final static HashMap<String, String> PARAMETERS = new HashMap<String, String>();
	public final static String PAGE = "http://deviouspk.com/play/";

	public static void init() {
		try {
			parseParameters();
			downloadJar();
		} catch (Exception ignore){
			ignore.printStackTrace();
		}
	}

	public static void downloadJar() {
		try {
			File file = new File(getPublicCache()+"Client"+File.separator+PARAMETERS.get("jar_name"));
			try {
				file.getParentFile().mkdirs();
			} catch (Exception ignore){
				ignore.printStackTrace();
			}
			BufferedInputStream input = new BufferedInputStream(new URL(PARAMETERS.get("archive")).openStream());
			BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
			int bytesRead = 0;
			while((bytesRead = input.read()) != -1) {
				output.write(bytesRead);
			}
			input.close();
			output.close();
		} catch (Exception ignore){
			ignore.printStackTrace();
		}
	}

	public static java.applet.Applet getApplet() {
		try {
			URLClassLoader classLoader = getClassLoader();
			if(classLoader != null) {
				Class<?> client = classLoader.loadClass(PARAMETERS.get("code").replace(".class", ""));
				java.applet.Applet applet = (java.applet.Applet) client.newInstance();
				applet.setBackground(Color.BLACK);
				applet.setStub(new Stub());
				return applet;
			}
		} catch (Exception ignore){
			ignore.printStackTrace();
		}
		return null;
	}

	public static URLClassLoader getClassLoader() {
		try {
			URLClassLoader classLoader = new URLClassLoader(new URL[]{new File(getPublicCache()+"Client"+File.separator+PARAMETERS.get("jar_name")).toURI().toURL()});
			classLoader.loadClass(PARAMETERS.get("code").replace(".class", ""));
			return classLoader;
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	private static void parseParameters() throws Exception {
		List<String> pageSource = getPageSource(new ArrayList<String>());
		if(pageSource.size() <= 0) {
			loadParameters();
		} else {
			for (String line : pageSource) {
				if (line.contains("<param")) {
					String key = line.split("<param name=\"")[1].split("\" ")[0];
					String value = line.split("value=\"")[1].split("\">")[0];
					if (key.equalsIgnoreCase("archive")) {
						PARAMETERS.put(key, value.contains("http") ? value : PAGE + value);
						continue;
					}
					PARAMETERS.put(key, value.isEmpty() ? " " : value);
				}
				if (line.contains("<applet")) {
					String[] parts = line.substring(7, line.length() - 1).replace("\"", "").split(" ");
					for (String part : parts) {
						if (part.contains("=")) {
							String key = part.substring(0, part.indexOf("="));
							String value = part.substring(part.indexOf("=") + 1);
							if (key.equalsIgnoreCase("archive")) {
								PARAMETERS.put(key, value.contains("http") ? value : PAGE + value);
								continue;
							}
							PARAMETERS.put(key, value.isEmpty() ? " " : value);
						}
					}
				}
			}
			saveParameters();
		}
	}

	private static List<String> getPageSource(ArrayList<String> pageSource) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(PAGE).openStream()));
			try {
				String line;
				while ((line = reader.readLine()) != null) {
					pageSource.add(line);
				}
			} finally {
				reader.close();
			}
		} catch (Exception ignored){}
		return pageSource;
	}

	private static void loadParameters() {
		File params = new File(getPublicCache() + "Client" + File.separator, "params.txt");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(params));
			String line;
			while ((line = reader.readLine()) != null) {
				if(line.isEmpty())
					continue;
				String[] args = line.split("=");
				if(args.length == 2) {
					PARAMETERS.put(args[0], args[1]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void saveParameters() {
		File params = new File(getPublicCache() + "Client" + File.separator, "params.txt");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(params));
			for(Entry<String, String> param : PARAMETERS.entrySet()) {
				writer.write(param.getKey()+"="+param.getValue());
				writer.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
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
}
