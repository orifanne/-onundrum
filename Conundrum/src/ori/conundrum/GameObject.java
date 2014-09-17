package ori.conundrum;

import java.util.ArrayList;

/**
 * Представляет игровой объект - единицу, имеющую набор 3d-моделей и координаты.
 * 
 * @author orifanne
 * 
 */
public abstract class GameObject {

	/**
	 * Координаты.
	 */
	private float x = 0;
	private float y = 0;
	private float z = 0;

	/**
	 * Признак видимости.
	 */
	private boolean isVisible = true;

	/**
	 * Набор 3d-моделей.
	 */
	private ArrayList<Model3D> models;

	
	
	public GameObject(float x, float y, float z, ArrayList<Model3D> models) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.models = models;
	}
	
	
	
	/**
	 * Отрисовать объект.
	 */
	public abstract void draw();
	

	
	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public void setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
