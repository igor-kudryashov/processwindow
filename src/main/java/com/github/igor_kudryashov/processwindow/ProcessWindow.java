package com.github.igor_kudryashov.processwindow;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class ProcessWindow {
	private final static String NEW_LINE = System.getProperty("line.separator");

	public static final int MODE_CLOSEABLE = 1;
	public static final int MODE_CANCELABLE = 2;

	private final static int PANEL_WIDTH = 520;
	private final static int PANEL_HEIGHT = 400;

	private static JTextArea taskOutput;
	
	// Process execution
	protected static final JFrame frame = new JFrame("Выполнение задачи"); 
	
	private static JProgressBar progressBar;
	private static JButton button;
	private boolean canceled = false;
	private int mode = 0;

	public ProcessWindow() {
		setIconImage();
		frame.setMinimumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

		// Center the window on screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((dim.width / 2) - (PANEL_WIDTH / 2), (dim.height / 2) - (PANEL_HEIGHT / 2));

		// Create and set up the content pane.
		BorderLayout layout = new BorderLayout();
		layout.setVgap(5);
		JComponent newContentPane = new JPanel(layout);
		// content panes must be opaque
		newContentPane.setOpaque(true); 

		frame.setContentPane(newContentPane);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		taskOutput = new JTextArea();

		taskOutput.setMargin(new Insets(5, 5, 5, 5));

		taskOutput.setEditable(false);
		taskOutput.setLineWrap(true);
		taskOutput.setWrapStyleWord(true);
		// Wait...
		button = new JButton("Ждите..."); 

		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				switch (mode) {
				case 1:
					frame.setVisible(false);
					frame.dispose();				
					break;
				case 2:
					 // Are you sure you want to cancel the execution of the task?
					if (JOptionPane.showConfirmDialog(frame, "Вы действительно хотите прервать выполнение задачи?",
							button.getText(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						canceled = true;
						setMode(MODE_CLOSEABLE);
					}
					break;

				default:
					break;
				}
			}
		});

		button.setEnabled(false);
		JScrollPane scrollPane = new JScrollPane(taskOutput);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		frame.add(scrollPane, BorderLayout.CENTER);
		JPanel buttonPane = new JPanel();
		buttonPane.add(button);
		frame.add(buttonPane, BorderLayout.PAGE_END);
		newContentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// Configure and add a progress bar
		progressBar = new JProgressBar();
		progressBar.setValue(0);
		progressBar.setMinimum(0);

		progressBar.setStringPainted(true);

		frame.add(progressBar, BorderLayout.NORTH);

		// Display the window.
		frame.pack();
	}

	public void setMode(int mode) {
		this.mode = mode;
		switch (mode) {
		case 1:
			frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			// Close
			button.setText("Закрыть"); 
			button.setEnabled(true);
			break;
		case 2:
			// Cancel
			button.setText("Отмена"); 
			button.setEnabled(true);
			break;
		default:
			break;
		}
	}

	public void setProgressVisible(boolean value) {
		progressBar.setVisible(value);
	}

	public void setProgress(int progress) {
		if (!progressBar.isIndeterminate()) {
			progressBar.setValue(progress);
		}
	}

	public void setProgressBarMax(int limit) {
		if (!progressBar.isIndeterminate()) {
			progressBar.setMaximum(limit);
		}
	}

	public void setIndeterminate(boolean value) {
		progressBar.setIndeterminate(value);

	}

	public void show() {
		try {
			frame.setAlwaysOnTop(true);
			// setAlwaysOnTop() throws java.security.AccessControlException in
			// Lotus Notes 9.0.1 and java.lang.SecurityException in earlier
			// versions. To fix this you must involve editing the
			// java.policy file (found in <Notes Folder>\JVM\lib\security) on
			// the user's machine and adding this permission:
			// permission java.awt.AWTPermission "setWindowAlwaysOnTop";
		} catch (java.security.AccessControlException eac) {
			// Lotus Notes 9.0.1
		} catch (java.lang.SecurityException es) {
			// earlier versions
		}
		frame.setVisible(true);
		frame.toFront();
	}

	public void hide() {
		frame.setVisible(false);
	}

	public void setTitle(String title) {
		frame.setTitle(title);
	}

	public void append(String s) {
		taskOutput.append(s);
	}

	public void print(String s) {
		append(s);
	}

	public void println(String s) {
		append(s + NEW_LINE);
		taskOutput.setCaretPosition(taskOutput.getDocument().getLength());
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setButtonText(String text) {
		button.setText(text);
	}

	public void dispose() {
		frame.setVisible(false);
		frame.dispose();
	}

	/**
	 * Set an image icon for window.
	 * If the file notes.gif does not have the resources, do nothing
	 */
	private void setIconImage() {		
		InputStream stream = getClass().getClassLoader().getResourceAsStream("notes.gif");
		if (stream != null) {
			Image image = null;
			try {
				image = ImageIO.read(stream);
				stream.close();
				frame.setIconImage(image);
			} catch (IOException e) {
				// do nothing
			}
		} // do nothing
	}
}
