package projekt;

import javax.swing.*;

public class Main {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				GUI frame = new GUI();
				frame.setVisible(true);
			}
		});
	}
}
