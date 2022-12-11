import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class AlarmClock {

	public static void main(String[] args) throws InterruptedException, IOException {
		boolean test = false;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			AlarmTimes.setAlarmTimes();
			System.out.println(i + " --- " + new Date() + "\n" + AlarmTimes.alarmStart + "-->" + AlarmTimes.alarmStop
					+ "\nAlarm will sound in " + 
					AlarmTimes.hms(LocalTime.now().until(AlarmTimes.alarmStart, ChronoUnit.SECONDS))+ "\n");
			if (AlarmTimes.isAlarmTime(test)) {

				AlarmSounds.soundAlarm(test);
				boolean continueAlarm = AlarmVisual.alert("Alarm",
						"Click OK to continue the alarm and play another vlc. Cancel to stop it", 50 * 1000);
				if (!continueAlarm) {
					AlarmSounds.killVlc();
					System.exit(0);
				}
			} else
				TimeUnit.SECONDS.sleep(1);
		}
	}
}

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
	public static ArrayList<String> getMp3s() {
		File folder = new File(System.getProperty("user.dir"));
		File[] listOfFiles = folder.listFiles();
		ArrayList<String> mp3s = new ArrayList<String>();
		for (int i = 0; i < listOfFiles.length; i++) 
			if (listOfFiles[i].isFile())
				if (listOfFiles[i].getName().endsWith(".mp3"))
					mp3s.add(listOfFiles[i].getName());
		return mp3s;
	}
	public static String pickMp3(ArrayList<String> mp3List) {
		Random r = new Random();
		int low = 0;
		int high = mp3List.size();
		int result = r.nextInt(high - low) + low;
		return mp3List.get(result);
	}
	public static int soundAlarm(boolean test) throws IOException {
		if (test) {
			java.awt.Toolkit.getDefaultToolkit().beep();
			return 60;
		}
		else {
			ArrayList<String> mp3List = getMp3s();
			String mp3 = pickMp3(mp3List);
			if (AlarmSounds.isLinux())
				Runtime.getRuntime().exec("vlc " + mp3);
			else {
				ProcessBuilder builder = new ProcessBuilder();
				builder.command("cmd.exe", "/c", "start vlc " + mp3);
				Process process = builder.start();
			}
			return mp3.startsWith("and") ? 50 : 264;//temporary fix until i get length of mp3s
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

	public static String hms(long seconds) {
		long hours = seconds / 3600;
		long minutes = (seconds % 3600) / 60;
		seconds = seconds % 60;

		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
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
