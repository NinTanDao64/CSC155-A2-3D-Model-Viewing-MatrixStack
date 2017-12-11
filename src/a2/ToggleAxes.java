package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class ToggleAxes extends AbstractAction {
	private Starter instance;
	
	public ToggleAxes (Starter ref) {
		super("Axes");
		this.instance = ref;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		instance.toggleAxes();
	}
}