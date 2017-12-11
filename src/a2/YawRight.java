package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class YawRight extends AbstractAction {
	private Camera camera;
	
	public YawRight (Camera cam) {
		super("Pan Right");
		this.camera = cam;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		camera.yawRight();
	}
}