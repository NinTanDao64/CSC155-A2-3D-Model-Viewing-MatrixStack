package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class MoveUp extends AbstractAction {
	private Camera camera;
	
	public MoveUp (Camera cam) {
		super("Up");
		this.camera = cam;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		camera.moveUp();
	}
}