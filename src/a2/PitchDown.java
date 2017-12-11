package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class PitchDown extends AbstractAction {
	private Camera camera;
	
	public PitchDown (Camera cam) {
		super("Look Down");
		this.camera = cam;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		camera.pitchDown();
	}
}