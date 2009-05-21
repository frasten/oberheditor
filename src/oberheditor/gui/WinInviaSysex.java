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
	private Shell win; // La finestra stessa
	private Scaletta scaletta;
	private Spinner txtChain;
	private ProgressBar progress;
	
	public WinInviaSysex(Shell parent, Scaletta _scaletta) {
		this.scaletta = _scaletta;
		
		win = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		win.setText("Scaletta");
		Display display = parent.getDisplay();
		
		
		int win_w = 300;
		int win_h = 170;
		// La metto centrata
		int pos_x = (display.getBounds().width - win_w) / 2;
		int pos_y = (display.getBounds().height - win_h) / 2;
		win.setBounds(pos_x, pos_y, win_w, win_h);
		
		FormLayout layout = new FormLayout();
		win.setLayout(layout);
		
		
		Label lblChain = new Label(win, SWT.NONE);
		lblChain.setText("Numero della chain: ");
		FormData layLblChain = new FormData();
		layLblChain.left = new FormAttachment(0, 10);
		layLblChain.top = new FormAttachment(0, 15);
		lblChain.setLayoutData(layLblChain);
		
		
		// Patch
		txtChain = new Spinner(win, SWT.BORDER);
		txtChain.setMinimum(1);
		txtChain.setMaximum(128);
		txtChain.setSelection(1);
		txtChain.setIncrement(1);
		txtChain.setPageIncrement(10);
		FormData layTxtChain = new FormData();
		layTxtChain.left = new FormAttachment(lblChain, 10);
		layTxtChain.top = new FormAttachment(lblChain, -3, SWT.TOP);
		txtChain.setLayoutData(layTxtChain);
		
		progress = new ProgressBar(win, SWT.HORIZONTAL);
		FormData layProgress = new FormData();
		layProgress.left = new FormAttachment(0, 20);
		layProgress.right = new FormAttachment(100, -20);
		layProgress.top = new FormAttachment(txtChain, 20, SWT.BOTTOM);
		progress.setLayoutData(layProgress);
		
		
		
		Button btnInvia = new Button(win, SWT.PUSH);
		btnInvia.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (inviaScaletta(txtChain.getSelection() - 1)) {
					// Se e' andato tutto bene, chiudo la finestra
					win.close();
				}
				else {
					MessageBox errore = new MessageBox(win, SWT.ICON_ERROR | SWT.OK);
					errore.setMessage("Errore di invio.");
					errore.setText("Errore");
					errore.open();
				}
			}
		});
		Image imgInvia = new Image(display, "res/send.png");
		btnInvia.setImage(imgInvia);
		btnInvia.setText("Invia!");
		FormData layBtnInvia = new FormData();
		layBtnInvia.right = new FormAttachment(100, -10);
		layBtnInvia.bottom = new FormAttachment(100, -10);
		btnInvia.setLayoutData(layBtnInvia);
		
		Button btnAnnulla = new Button(win, SWT.PUSH);
		btnAnnulla.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				win.close();
			}
		});
		Image imgAnnulla = new Image(display, "res/cancel.png");
		btnAnnulla.setImage(imgAnnulla);
		btnAnnulla.setText("Annulla");
		FormData layBtnAnnulla = new FormData();
		layBtnAnnulla.right = new FormAttachment(btnInvia, -10, SWT.LEFT);
		layBtnAnnulla.top = new FormAttachment(btnInvia, 0, SWT.TOP);
		btnAnnulla.setLayoutData(layBtnAnnulla);
		
		
		
		win.setDefaultButton(btnInvia);
		win.open();
		while (!win.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep ();
		}
	}

	private boolean inviaScaletta(int posizione_chain) {
		boolean tuttoOk = true;
		CreatoreMessaggi cm = new CreatoreMessaggi(scaletta);
		Vector<SysexMessage> messaggi = cm.creaMessaggi(posizione_chain);
		SysexTransmitter transmitter = new SysexTransmitter();
		progress.setSelection(0);
		for (int i = 0; i < messaggi.size(); i++) {
			if (!transmitter.invia(messaggi.get(i))) {
				tuttoOk = false;
				break;
			}
			progress.setSelection((i+1)*100/messaggi.size());
		}
		transmitter.close();
		return tuttoOk;
	}
}
