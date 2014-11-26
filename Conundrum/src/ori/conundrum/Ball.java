package ori.conundrum;

import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

public class Ball extends GameObject {

	/** Проекция скорости на ось X */
	private float vx = 0;
	/** Проекция скорости на ось Y */
	private float vy = 0;
	/** Масса шара */
	private float m = 1;
	/** Радиус шара */
	private float rad = 0.1f;
	/** Коэффициент трения качения */
	private float f = 0.005f;

	private Coords boundLeftDownFar;
	private Coords boundRightUpNear;

	/**
	 * @param coords
	 * @param models
	 * @param b1
	 * @param b2
	 * @param rad
	 */
	Ball(Coords coords, HashMap<Model3D, ArrayList<Coords>> models, Coords b1,
			Coords b2, float rad) {
		super(coords, models);
		boundLeftDownFar = b1;
		boundRightUpNear = b2;
		this.rad = rad;
	}

	public void countCoords() {
		/*
		 * float angleX, angleY; if (MainActivity.xAngle == 0) angleX = (float)
		 * (Math.PI * 0.5 + MainActivity.xAngle); else angleX = (float) (Math.PI
		 * * 0.5 + (MainActivity.xAngle / Math .abs(MainActivity.xAngle))
		 * (Math.abs(Math.min(Math.abs(MainActivity.xAngle), Math.PI -
		 * Math.abs(MainActivity.xAngle)))));
		 * 
		 * angleY = (float) (Math.PI * 0.5 + MainActivity.yAngle);
		 */
		float angleX = MainActivity.xAngle;
		float angleY = MainActivity.yAngle;

		// с учетом только силы тяжести и силы трения качения
		float ax = (float) (0.098 * (Math.sin(angleX) - (f / rad * Math
				.cos(angleX))));
		float ay = (float) (0.098 * (Math.sin(angleY) - (f / rad * Math
				.cos(angleY))));

		vx += MainActivity.deltaT * 0.001 * ax;
		vy += MainActivity.deltaT * 0.001 * ay;

		float deltax = (float) (MainActivity.deltaT * 0.001 * vx + ((MainActivity.deltaT * 0.001)
				* (MainActivity.deltaT * 0.001) / 2)
				* ax);
		float deltay = (float) (MainActivity.deltaT * 0.001 * vy + ((MainActivity.deltaT * 0.001)
				* (MainActivity.deltaT * 0.001) / 2)
				* ay);

		if (coords.getX() + deltax <= 0)
			coords.setX(Math.max(boundLeftDownFar.getX() + rad, coords.getX()
					+ deltax));
		else
			coords.setX(Math.min(boundRightUpNear.getX() - rad, coords.getX()
					+ deltax));
		if (coords.getY() + deltay <= 0)
			coords.setY(Math.max(boundRightUpNear.getY() + rad, coords.getY()
					+ deltay));
		else
			coords.setY(Math.min(boundLeftDownFar.getY() - rad, coords.getY()
					+ deltay));
		
		// рассчет угла поворота
		
		

		// Log.d("***************", Float.toString(deltax));
	}
}
