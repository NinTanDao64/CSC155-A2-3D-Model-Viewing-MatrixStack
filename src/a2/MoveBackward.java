package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class MoveBackward extends AbstractAction {
	private Camera camera;
	
	public MoveBackward (Camera cam) {
		super("Backward");
		this.camera = cam;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		camera.moveBackward();
	}
}