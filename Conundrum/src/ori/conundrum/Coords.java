package ori.conundrum;

/**
 * Представляет тройку координат.
 * 
 * @author orifanne
 * 
 */
public class Coords {

	private float x = 0;
	private float y = 0;
	private float z = 0;
	private float xAngle = 0;
	private float yAngle = 0;

	public Coords(float x, float y, float z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float getXAngle() {
		return xAngle;
	}

	public void setXAngle(float xAngle) {
		this.xAngle = xAngle;
	}

	public float getYAngle() {
		return yAngle;
	}

	public void setYAngle(float yAngle) {
		this.yAngle = yAngle;
	}
}
