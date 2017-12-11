package a1;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class HorizontalMovement extends AbstractAction {
	private Starter myForm;
	
	public HorizontalMovement (Starter fForm) {
		super("Left/Right");
		myForm = fForm;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		myForm.horizButton();
	}
}
