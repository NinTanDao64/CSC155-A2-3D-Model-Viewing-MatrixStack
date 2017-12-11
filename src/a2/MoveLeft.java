package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class MoveLeft extends AbstractAction {
	private Camera camera;
	
	public MoveLeft (Camera cam) {
		super("Left");
		this.camera = cam;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		camera.moveLeft();
	}
}