package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class MoveForward extends AbstractAction {
	private Camera camera;
	
	public MoveForward (Camera cam) {
		super("Forward");
		this.camera = cam;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		camera.moveForward();
	}
}