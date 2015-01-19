package ori.conundrum;

/**
 * Представляет тройку координат в пространстве и повороты вокруг осей x и y.
 * 
 * @author orifanne
 * 
 */
public class Coords {

	/** X координата */
	private float x = 0;
	/** Y координата */
	private float y = 0;
	/** Z координата */
	private float z = 0;
	/** Поворот вокруг оси X в градусах */
	private float xAngle = 0;
	/** Поворот вокруг оси Y в градусах */
	private float yAngle = 0;

	/**
	 * @param x
	 *            X координата
	 * @param y
	 *            Y координата
	 * @param z
	 *            Z координата
	 */
	public Coords(float x, float y, float z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * @return X координата
	 */
	public float getX() {
		return x;
	}

	/**
	 * @param x
	 *            X координата
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * @return Y координата
	 */
	public float getY() {
		return y;
	}

	/**
	 * @param y
	 *            Y координата
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * @return Z координата
	 */
	public float getZ() {
		return z;
	}

	/**
	 * @param z
	 *            Z координата
	 */
	public void setZ(float z) {
		this.z = z;
	}

	/**
	 * @return Поворот вокруг оси X в градусах
	 */
	public float getXAngle() {
		return xAngle;
	}

	/**
	 * @param xAngle
	 *            Поворот вокруг оси X в градусах
	 */
	public void setXAngle(float xAngle) {
		this.xAngle = xAngle;
	}

	/**
	 * @return Поворот вокруг оси Y в градусах
	 */
	public float getYAngle() {
		return yAngle;
	}

	/**
	 * @param yAngle
	 *            Поворот вокруг оси X в градусах
	 */
	public void setYAngle(float yAngle) {
		this.yAngle = yAngle;
	}
}
