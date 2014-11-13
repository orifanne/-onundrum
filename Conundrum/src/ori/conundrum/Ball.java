package ori.conundrum;

import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

public class Ball extends GameObject {

	private float vx = 0;
	private float vy = 0;
	private float m = 1;

	Ball(Coords coords, HashMap<Model3D, ArrayList<Coords>> models) {
		super(coords, models);
	}

	public void countCoords() {
		/*float angleX, angleY;
		if (MainActivity.xAngle == 0)
			angleX = (float) (Math.PI * 0.5 + MainActivity.xAngle);
		else
			angleX = (float) (Math.PI * 0.5 + (MainActivity.xAngle / Math
					.abs(MainActivity.xAngle))
					* (Math.abs(Math.min(Math.abs(MainActivity.xAngle), Math.PI
							- Math.abs(MainActivity.xAngle)))));

		angleY = (float) (Math.PI * 0.5 + MainActivity.yAngle);*/
		float angleX = MainActivity.xAngle;
		float angleY = MainActivity.yAngle;

		// с учетом только силы тяжести
		float ax = (float) (0.098 * Math.sin(angleX));
		float ay = (float) (0.098 * Math.sin(angleY));

		vx += MainActivity.deltaT * 0.001 * ax;
		vy += MainActivity.deltaT * 0.001 * ay;

		float deltax = (float) (MainActivity.deltaT * 0.001 * vx + ((MainActivity.deltaT * 0.001)
				* (MainActivity.deltaT * 0.001) / 2)
				* ax);
		float deltay = (float) (MainActivity.deltaT * 0.001 * vy + ((MainActivity.deltaT * 0.001)
				* (MainActivity.deltaT * 0.001) / 2)
				* ay);

		coords.setX(coords.getX() + deltax);
		coords.setY(coords.getY() + deltay);

		Log.d("***************", Float.toString(deltax));
	}
}
