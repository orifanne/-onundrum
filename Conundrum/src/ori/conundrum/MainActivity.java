package ori.conundrum;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

@SuppressLint("DefaultLocale")
public class MainActivity extends Activity {

	TextView tvText;
	SensorManager sensorManager;
	Sensor sensorAccel;
	Sensor sensorLinAccel;
	Sensor sensorGravity;
	Sensor sensorGyr;

	StringBuilder sb = new StringBuilder();

	Timer timer;

	float[] valuesAccel = new float[3];
	float[] valuesLinAccel = new float[3];
	float[] valuesGravity = new float[3];
	float[] valuesGyr = new float[3];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tvText = (TextView) findViewById(R.id.tvText);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorLinAccel = sensorManager
				.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		sensorGyr = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		sensorManager.registerListener(listener, sensorAccel,
				SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(listener, sensorLinAccel,
				SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(listener, sensorGravity,
				SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(listener, sensorGyr,
				SensorManager.SENSOR_DELAY_NORMAL);

		timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						showInfo();
					}
				});
			}
		};
		timer.schedule(task, 0, 400);
	}

	@Override
	protected void onPause() {
		super.onPause();
		sensorManager.unregisterListener(listener);
		timer.cancel();
	}

	String format(float values[]) {
		return String.format("%1$.1f\t\t%2$.1f\t\t%3$.1f", values[0],
				values[1], values[2]);
	}

	void showInfo() {
		sb.setLength(0);
		sb.append("Accelerometer: " + format(valuesAccel))
				.append("\n\nLin accel : " + format(valuesLinAccel))
				.append("\nGravity : " + format(valuesGravity))
				.append("\n\nGyroscope : " + format(valuesGyr));
		tvText.setText(sb);
	}

	SensorEventListener listener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				for (int i = 0; i < 3; i++) {
					valuesAccel[i] = event.values[i];
				}
				break;
			case Sensor.TYPE_LINEAR_ACCELERATION:
				for (int i = 0; i < 3; i++) {
					valuesLinAccel[i] = event.values[i];
				}
				break;
			case Sensor.TYPE_GRAVITY:
				for (int i = 0; i < 3; i++) {
					valuesGravity[i] = event.values[i];
				}
				break;
			case Sensor.TYPE_GYROSCOPE:
				for (int i = 0; i < 3; i++) {
					valuesGyr[i] = event.values[i];
				}
				break;
			}

		}

	};

}
