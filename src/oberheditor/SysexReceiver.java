package oberheditor;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.SysexMessage;

public class SysexReceiver implements Receiver {

	public void close() {
		// TODO Auto-generated method stub
		
	}

	public void send(MidiMessage message, long timeStamp) {
		if (!(message instanceof SysexMessage)) return;
		
		byte[] bytes = message.getMessage();
		System.out.println(getHexString(bytes));
		MidiReader.messaggi.add((SysexMessage) message);
	}
	
	
	private static char hexDigits[] = 
  {'0', '1', '2', '3', 
   '4', '5', '6', '7', 
   '8', '9', 'A', 'B', 
   'C', 'D', 'E', 'F'};

	public static String getHexString(byte[] aByte)
	{
		StringBuffer	sbuf = new StringBuffer(aByte.length * 3 + 2);
		for (int i = 0; i < aByte.length; i++)
		{
			sbuf.append(' ');
			sbuf.append(hexDigits[(aByte[i] & 0xF0) >> 4]);
			sbuf.append(hexDigits[aByte[i] & 0x0F]);
		}
		return new String(sbuf);
	}

}
