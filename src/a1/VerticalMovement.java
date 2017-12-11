package a1;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class VerticalMovement extends AbstractAction {
	private Starter myForm;
	
	public VerticalMovement (Starter fForm) {
		super("Up/Down");
		myForm = fForm;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		myForm.vertButton();
	}
}
