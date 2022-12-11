package alarm_clock;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class AlarmClock {

	public static void main(String[] args) throws InterruptedException, IOException {
		boolean test = false;;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			System.out.println(i + " --- " + new Date());
			AlarmTimes.setAlarmTimes();
			if (AlarmTimes.isAlarmTime(test)) {

				AlarmSounds.soundAlarm(test);
				boolean continueAlarm = AlarmVisual.alert("Alarm",
						"Click OK to continue the alarm and play another vlc. Cancel to stop it", 50 * 1000);
				if (!continueAlarm) {
					AlarmSounds.killVlc();
					System.exit(0);
				}
			} else
				TimeUnit.SECONDS.sleep(60);
		}
	}
}

// java.awt.Toolkit.getDefaultToolkit().beep();
class AlarmVisual {
	public static boolean alert(String title, String message, int timeout) {
		JLabel lbmsg = new JLabel(message);
		return AlarmVisual.showConfirmDialogWithTimeout(lbmsg, title, timeout);
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

class AlarmSounds {
	public static boolean isLinux() {
		return System.getProperty("os.name") == "Linux";
	}

	public static void soundAlarm(boolean test) throws IOException {
		if (test)
			java.awt.Toolkit.getDefaultToolkit().beep();
		else if (AlarmSounds.isLinux())
			Runtime.getRuntime().exec("vlc andrew-schultz.mp3");
		else {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command("cmd.exe", "/c", "start vlc andrew-schultz.mp3");
			Process process = builder.start();
		}
	}

	public static void killVlc() throws IOException {
		if (AlarmSounds.isLinux())
			Runtime.getRuntime().exec("killall vlc");
		else {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command("cmd.exe", "/c", "TASKKILL /IM VLC.EXE");
			Process process = builder.start();
		}
	}
}

class AlarmTimes {
	static LocalTime alarmStart;
	static LocalTime alarmStop;

	public static boolean isAlarmTime(boolean test) {
		if (test)
			return true;
		else {
			LocalTime now = LocalTime.now();
			return now.compareTo(AlarmTimes.alarmStart) == 1 && now.compareTo(AlarmTimes.alarmStop) == -1;
		}
	}

	public static void setAlarmTimes() {
		int day = getDayNumberOld(new Date());
		if (day == 7) {
			AlarmTimes.alarmStart = LocalTime.of(6, 00);
			AlarmTimes.alarmStop = LocalTime.of(6, 02);
		} else {
			AlarmTimes.alarmStart = LocalTime.of(5, 15);
			AlarmTimes.alarmStop = LocalTime.of(5, 35);
		}
	}

	public static int getDayNumberOld(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_WEEK);
	}
}
