package a1;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class CircMovement extends AbstractAction {
	private Starter myForm;
	
	public CircMovement (Starter fForm) {
		super("Circular");
		myForm = fForm;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		myForm.circButton();
	}
}