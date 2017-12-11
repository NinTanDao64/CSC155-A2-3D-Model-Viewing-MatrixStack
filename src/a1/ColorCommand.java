package a1;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class ColorCommand extends AbstractAction {
	private Starter myForm;
	
	public ColorCommand (Starter fForm) {
		super("Change Color");
		myForm = fForm;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		myForm.changeColor();
	}
}
