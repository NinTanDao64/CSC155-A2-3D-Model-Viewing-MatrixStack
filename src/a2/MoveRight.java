package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class MoveRight extends AbstractAction {
	private Camera camera;
	
	public MoveRight (Camera cam) {
		super("Right");
		this.camera = cam;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		camera.moveRight();
	}
}