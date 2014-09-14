package ori.conundrum;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

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

	private static final float NS2S = 1.0f / 1000000000.0f;
	private static final float EPSILON = 1.0f / 1000000000.0f;
	private final float[] deltaRotationVector = new float[4];
	private float timestamp;
	float[] deltaRotationMatrix = new float[9];
	public static float[] rotationCurrent = new float[3];

	// создадим ссылку на экземпляр нашего класса MyClassSurfaceView
	private GLSurfaceView mGLSurfaceView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mGLSurfaceView = new GLSurfaceView(this);

		// Проверяем поддереживается ли OpenGL ES 2.0.
		final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo = activityManager
				.getDeviceConfigurationInfo();
		final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

		if (supportsEs2) {
			// Запрос OpenGL ES 2.0 для установки контекста.
			mGLSurfaceView.setEGLContextClientVersion(2);

			// Устанавливаем рендеринг
			mGLSurfaceView.setRenderer(new MyClassRenderer());
			mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		} else {
			// Устройство поддерживает только OpenGL ES 1.x
			// ... печаль
			return;
		}

		setContentView(mGLSurfaceView);

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
		// if (id == R.id.action_settings) {
		// return true;
		// }
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLSurfaceView.onResume();
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
						// showInfo();
					}
				});
			}
		};
		timer.schedule(task, 0, 400);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLSurfaceView.onPause();
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

				// This timestep's delta rotation to be multiplied by the
				// current rotation
				// after computing it from the gyro sample data.
				if (timestamp != 0) {
					final float dT = (event.timestamp - timestamp) * NS2S;
					// Axis of the rotation sample, not normalized yet.
					float axisX = event.values[0];
					float axisY = event.values[1];
					float axisZ = event.values[2];

					// Calculate the angular speed of the sample
					float omegaMagnitude = (float) Math.sqrt(axisX * axisX
							+ axisY * axisY + axisZ * axisZ);

					// Normalize the rotation vector if it's big enough to get
					// the axis
					if (omegaMagnitude > EPSILON) {
						axisX /= omegaMagnitude;
						axisY /= omegaMagnitude;
						axisZ /= omegaMagnitude;
					}

					// Integrate around this axis with the angular speed by the
					// timestep
					// in order to get a delta rotation from this sample over
					// the timestep
					// We will convert this axis-angle representation of the
					// delta rotation
					// into a quaternion before turning it into the rotation
					// matrix.
					float thetaOverTwo = omegaMagnitude * dT / 2.0f;
					float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
					float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
					deltaRotationVector[0] = sinThetaOverTwo * axisX;
					deltaRotationVector[1] = sinThetaOverTwo * axisY;
					deltaRotationVector[2] = sinThetaOverTwo * axisZ;
					deltaRotationVector[3] = cosThetaOverTwo;
				}
				timestamp = event.timestamp;
				float[] deltaRotationMatrix = new float[9];
				SensorManager.getRotationMatrixFromVector(deltaRotationMatrix,
						deltaRotationVector);
				// User code should concatenate the delta rotation we computed
				// with the current rotation
				// in order to get the updated rotation.
				float[] k = new float[3];

				k[0] = rotationCurrent[0] * deltaRotationMatrix[0]
						+ rotationCurrent[1] * deltaRotationMatrix[3]
						+ rotationCurrent[2] * deltaRotationMatrix[6];
				k[1] = rotationCurrent[0] * deltaRotationMatrix[1]
						+ rotationCurrent[1] * deltaRotationMatrix[4]
						+ rotationCurrent[2] * deltaRotationMatrix[7];
				k[2] = rotationCurrent[0] * deltaRotationMatrix[2]
						+ rotationCurrent[1] * deltaRotationMatrix[5]
						+ rotationCurrent[2] * deltaRotationMatrix[8];
				rotationCurrent = k;
				break;
			}

		}

	};

}
