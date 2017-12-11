package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class PitchUp extends AbstractAction {
	private Camera camera;
	
	public PitchUp (Camera cam) {
		super("Look Up");
		this.camera = cam;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		camera.pitchUp();
	}
}