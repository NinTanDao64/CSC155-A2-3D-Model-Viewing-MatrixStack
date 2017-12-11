package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class MoveDown extends AbstractAction {
	private Camera camera;
	
	public MoveDown (Camera cam) {
		super("Down");
		this.camera = cam;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		camera.moveDown();
	}
}