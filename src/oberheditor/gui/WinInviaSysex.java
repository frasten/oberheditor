package oberheditor.gui;

import java.util.Vector;

import javax.sound.midi.SysexMessage;

import oberheditor.Scaletta;
import oberheditor.midi.CreatoreMessaggi;
import oberheditor.midi.SysexTransmitter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;


public class WinInviaSysex {
	Shell win; // La finestra stessa
	Scaletta scaletta;
	private Spinner txtPatch;
	
	public WinInviaSysex(Shell parent, Scaletta _scaletta) {
		this.scaletta = _scaletta;
		
		win = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		win.setText("Scaletta");
		Display display = parent.getDisplay();
		
		
		int win_w = 400;
		int win_h = 300;
		// La metto centrata
		int pos_x = (display.getBounds().width - win_w) / 2;
		int pos_y = (display.getBounds().height - win_h) / 2;
		win.setBounds(pos_x, pos_y, win_w, win_h);
		
		FormLayout layout = new FormLayout();
		win.setLayout(layout);
		
		
		// Patch
		txtPatch = new Spinner(win, SWT.BORDER);
		txtPatch.setMinimum(1);
		txtPatch.setMaximum(128);
		txtPatch.setSelection(1);
		txtPatch.setIncrement(1);
		txtPatch.setPageIncrement(10);
		FormData layTxtPatch = new FormData();
		layTxtPatch.left = new FormAttachment(0, 10);
		layTxtPatch.top = new FormAttachment(0, 10);
		txtPatch.setLayoutData(layTxtPatch);
		
		
		
		Button btnInvia = new Button(win, SWT.PUSH);
		btnInvia.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				inviaScaletta(1);
				//win.close();
			}
		});
		Image imgInvia = new Image(display, "res/save.png");
		btnInvia.setImage(imgInvia);
		btnInvia.setText("Invia!");
		FormData layBtnInvia = new FormData();
		layBtnInvia.right = new FormAttachment(100, -10);
		layBtnInvia.bottom = new FormAttachment(100, -10);
		btnInvia.setLayoutData(layBtnInvia);
		
		
		win.open();
		while (!win.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep ();
		}
	}

	private void inviaScaletta(int posizione_chain) {
		CreatoreMessaggi cm = new CreatoreMessaggi(scaletta);
		Vector<SysexMessage> messaggi = cm.creaMessaggi(txtPatch.getSelection() - 1);
		SysexTransmitter transmitter = new SysexTransmitter();
		for (int i = 0; i < messaggi.size(); i++) {
			// TODO: aggiornare la progressbar con percentuale (i+1)*100/messaggi.size()
			if (!transmitter.invia(messaggi.get(i))) break;
		}
		transmitter.close();
	}
}
