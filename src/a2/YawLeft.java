package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class YawLeft extends AbstractAction {
	private Camera camera;
	
	public YawLeft (Camera cam) {
		super("Pan Left");
		this.camera = cam;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		camera.yawLeft();
	}
}