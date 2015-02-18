package ori.conundrum;

import java.util.ArrayList;

/**
 * Металлический шарик.
 * 
 * @author orifanne
 * 
 */

public class Ball extends FlexibleGameObject {

	/** Проекция скорости на ось X */
	private float vx = 0;
	/** Проекция скорости на ось Y */
	private float vy = 0;
	/** Масса шара */
	private float m = 1;
	/** Радиус шара */
	private float rad = 0.1f;
	/** Коэффициент трения качения */
	private float f = 0.001f;

	/** Граница объемной области передвижения слева снизу дальняя */
	private Coords boundLeftDownFar;
	/** Граница объемной области передвижения справа сверху ближняя */
	private Coords boundRightUpNear;

	/**
	 * @param coords
	 *            координаты центра
	 * @param model
	 *            3D модель
	 * @param b1
	 *            граница
	 * @param b2
	 *            граница
	 * @param rad
	 *            радиус
	 */
	Ball(Coords coords, Model3D model, Coords b1, Coords b2,
			float rad) {
		super(coords, model);
		boundLeftDownFar = b1;
		boundRightUpNear = b2;
		this.rad = rad;
	}

	/**
	 * Пересчитать координаты центра в соответствии с текущим состоянием (наклон
	 * устройства)
	 */
	public void countCoords() {

		float angleX = MainActivity.xAngle;
		float angleY = MainActivity.yAngle;

		// с учетом только силы тяжести и силы трения качения
		float ax = (float) (0.098 * (Math.sin(angleX) - (f / rad * Math
				.cos(angleX))));
		float ay = (float) (0.098 * (Math.sin(angleY) - (f / rad * Math
				.cos(angleY))));
		
		vx += MainActivity.deltaT * 0.001 * ax;
		vy += MainActivity.deltaT * 0.001 * ay;

		float deltax = (float) (MainActivity.deltaT * 0.001 * (vx) + ((MainActivity.deltaT * 0.001)
				* (MainActivity.deltaT * 0.001) / 2)
				* ax);
		float deltay = (float) (MainActivity.deltaT * 0.001 * (vy) + ((MainActivity.deltaT * 0.001)
				* (MainActivity.deltaT * 0.001) / 2)
				* ay);

		float x = coords.getX();
		float y = coords.getY();

		coords.setX(Math.max(
				Math.min(boundRightUpNear.getX() - rad, x + deltax),
				boundLeftDownFar.getX()));

		coords.setY(Math.max(
				Math.min(boundLeftDownFar.getY() - rad, y + deltay),
				boundRightUpNear.getY()));

		deltax = x - coords.getX();
		deltay = y - coords.getY();

		// рассчет угла поворота

		float deltaYAngle = 0, deltaXAngle = 0;

		float c = (float) (2 * Math.PI * rad);

		if (deltax != 0)
			deltaYAngle = (float) (360 * (deltax % c) / c);
		if (deltay != 0)
			deltaXAngle = (float) (360 * (deltay % c) / c);

		coords.setXAngle((coords.getXAngle() + deltaXAngle) % 360);
		coords.setYAngle((coords.getYAngle() + deltaYAngle) % 360);
	}
}
