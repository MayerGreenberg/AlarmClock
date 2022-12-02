package alarm_clock;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class AlarmClock {

	public static void main(String[] args) throws InterruptedException, IOException {
		var alarmStart = LocalTime.of(5,15);
		var alarmStop = LocalTime.of(5, 35);
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			var now = LocalTime.now();
			var inBetween = true;//now.compareTo(alarmStart) == 1 && now.compareTo(alarmStop) == -1;
			if(inBetween) {
//				java.awt.Toolkit.getDefaultToolkit().beep();
				soundAlarm();
				var continueAlarm = alert("Alarm", "Click OK to continue the alarm and play another vlc. Cancel to stop it", 50 * 1000);
				if(!continueAlarm) {
					killVlc();
					System.exit(0);
				}
			}
			//TimeUnit.SECONDS.sleep(50);
		}
	}
	public static boolean alert(String title, String message, int timeout) {
		JLabel lbmsg = new JLabel(message);
		return showConfirmDialogWithTimeout(lbmsg, title, timeout);
	}
	public static void soundAlarm() throws IOException {
		ProcessBuilder builder = new ProcessBuilder();
		builder.command("cmd.exe", "/c", "start vlc andrew-schultz.mp3");
		Process process = builder.start();
	}
	public static void killVlc() throws IOException {
		ProcessBuilder builder = new ProcessBuilder();
		builder.command("cmd.exe", "/c", "TASKKILL /IM VLC.EXE");
		Process process = builder.start();
	}
	public final static boolean showConfirmDialogWithTimeout(Object params, String title, int timeout_ms) {
		final JOptionPane msg = new JOptionPane(params, JOptionPane.WARNING_MESSAGE, JOptionPane.CANCEL_OPTION);
		final JDialog dlg = msg.createDialog(title);

		msg.setInitialSelectionValue(JOptionPane.OK_OPTION);
		dlg.setAlwaysOnTop(true);
		dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dlg.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				super.componentShown(e);
				final Timer t = new Timer(timeout_ms, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						dlg.setVisible(false);
					}

				});
				t.start();
			}
		});
		dlg.setVisible(true);

		Object selectedvalue = msg.getValue();
		if (selectedvalue.equals(JOptionPane.CANCEL_OPTION)) {
			return false;
		} else {
			return true;
		}
	}
}
