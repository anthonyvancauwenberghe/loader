package loader;

import loader.client.Client;
import loader.ui.Resource;
import loader.ui.TabUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;

public class Loader extends JFrame implements ActionListener {

	private JPanel contentPane;
	private JTabbedPane tabbedPane;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem newTab;
	private JMenuItem closeTab;
	private int tabIndex = 1;

    public static boolean DEV = false;

	public static void main(String[] args) throws IOException, InvocationTargetException, InterruptedException {
		RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
		java.util.List<String> arguments = runtimeMxBean.getInputArguments();
		StringBuilder arg = new StringBuilder();
		for (String argument : arguments)
            arg.append(argument);

        if(args.length > 0 && args[0].toString().contains("-dev")) {
            DEV = true;
            System.out.println("Loading in DEV Mode...");
        }

		String name = Loader.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        System.out.println("name: " + name);
		name = URLDecoder.decode(name, "UTF-8");
		name = name.substring(name.lastIndexOf("/") + 1);
		if (!arg.toString().contains("-Xmx")) {
            String[] argus = DEV ? new String[]{"java", "-Xmx512m", "-jar", name, "-dev"} : new String[]{"java", "-Xmx512m", "-jar", name};
			if (!name.isEmpty())
				Runtime.getRuntime().exec(argus);
			else
				JOptionPane.showMessageDialog(null, "Please start the loader with > 512m of memory. ");
		} else {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					Client.init();

					Loader loader = new Loader();
					loader.initGUI();
					loader.showGUI();
				}
			});
		}
	}

	public void initGUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			JPopupMenu.setDefaultLightWeightPopupEnabled(false);

			setIconImages(Resource.getIconImages());
		} catch (Exception ignore) {
		}

		contentPane = (JPanel) getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.setPreferredSize(new Dimension(765, 524));

		tabbedPane = new JTabbedPane();
		tabbedPane.setUI(new TabUI());
		tabbedPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		menuBar = new JMenuBar();

		fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		newTab = new JMenuItem("Open New Tab");
		newTab.addActionListener(this);
		fileMenu.add(newTab);

		closeTab = new JMenuItem("Close Current Tab");
		closeTab.addActionListener(this);
		fileMenu.add(closeTab);
	}

	public void showGUI() {

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setJMenuBar(menuBar);
		setTitle("ArteroPK " + (DEV ? "BETA" : ""));
		setContentPane(contentPane);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);

		openNewTab();
	}

	private void openNewTab() {
		Applet applet = Client.getApplet();
		if (applet != null) {
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(applet, BorderLayout.CENTER);
			tabbedPane.addTab("Client #" + (tabIndex++), panel);

			applet.init();
			applet.start();
			applet.requestFocus();
			applet.requestFocusInWindow();
		} else {
			JOptionPane.showMessageDialog(this, "Could not open new client.");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equalsIgnoreCase("Open New Tab")) {
			openNewTab();
		}

		if (e.getActionCommand().equalsIgnoreCase("Close Current Tab")) {
			new SwingWorker<Void, Void>() {
				protected Void doInBackground() throws Exception {

					if (tabbedPane.getSelectedComponent() instanceof JPanel) {

						JPanel panel = (JPanel) tabbedPane.getSelectedComponent();
						Applet applet = (Applet) panel.getComponent(0);
						panel.removeAll();

						int index = tabbedPane.getSelectedIndex();
						tabbedPane.setComponentAt(tabbedPane.getSelectedIndex(), null);
						tabbedPane.remove(index);
						if (index < tabbedPane.getTabCount()) {
							tabbedPane.setSelectedIndex(index);
						} else {
							if (tabbedPane.getTabCount() > 0)
								tabbedPane.setSelectedIndex(0);
						}
						validate();
						repaint();

						applet.stop();
						applet.destroy();
						System.gc();
					}
					return null;
				}
			}.execute();
		}
	}
}
