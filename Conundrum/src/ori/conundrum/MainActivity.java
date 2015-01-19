package ori.conundrum;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	/** Промежуток времени, через который обновляется кадр (в миллисекундах) */
	public static int deltaT = 60;
	/** Менеджер датчиков */
	SensorManager sensorManager;
	/** Акселерометр */
	Sensor sensorAccel;
	/** Датчик магнитного поля */
	Sensor sensorMagnet;

	/** Надо ли разворачивать оси, потому что ориентация по умолчанию портретная */
	boolean flip = false;

	/** Угол наклона относительно оси X (в ) */
	public static float xAngle;
	/** Угол наклона относительно оси Y (в ) */
	public static float yAngle;

	/** Таймер (для задания обновления информации с датчиков) */
	Timer timer;

	/** Текущие значения на акселерометре */
	float[] valuesAccel = new float[3];
	/** Текущие значения на датчике магнитного поля */
	float[] valuesMagnet = new float[3];

	/** Текущая матрица поворота */
	float[] rotationCurrent = new float[3];

	/** Renderer */
	private GLSurfaceView mGLSurfaceView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// определяем, где у устройства верх
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		if (size.x < size.y)
			flip = true;

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorMagnet = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

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
			mGLSurfaceView.setRenderer(new MyClassRenderer(this
					.getBaseContext()));
			mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		} else {
			// Устройство поддерживает только OpenGL ES 1.x
			// ... печаль
			return;
		}

		setContentView(mGLSurfaceView);
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
		sensorManager.registerListener(listener, sensorMagnet,
				SensorManager.SENSOR_DELAY_NORMAL);

		timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						getDeviceOrientation();
					}
				});
			}
		};
		timer.schedule(task, 0, deltaT);
	}

	/**
	 * Обновить информацию об углах наклона устройства
	 */
	void getDeviceOrientation() {
		float[] r = new float[9];
		SensorManager.getRotationMatrix(r, null, valuesAccel, valuesMagnet);
		SensorManager.getOrientation(r, rotationCurrent);
		if (flip) {
			xAngle = rotationCurrent[1];
			yAngle = rotationCurrent[2];
		} else {
			xAngle = rotationCurrent[2];
			yAngle = rotationCurrent[1];
		}
		return;
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLSurfaceView.onPause();
		sensorManager.unregisterListener(listener);
		timer.cancel();
	}

	/** Слушатель изменений датчиков */
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
			case Sensor.TYPE_MAGNETIC_FIELD:
				for (int i = 0; i < 3; i++) {
					valuesMagnet[i] = event.values[i];
				}
				break;
			}

		}

	};

}
