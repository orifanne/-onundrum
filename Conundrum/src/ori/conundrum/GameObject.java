package ori.conundrum;

import java.util.ArrayList;
import java.util.HashMap;

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
	private Coords coords;

	/**
	 * Признак видимости.
	 */
	private boolean isVisible = true;

	/**
	 * Набор 3d-моделей с координатами.
	 */
	private ArrayList<HashMap<Model3D, ArrayList<Coords>>> models;

	
	
	public GameObject(Coords coords,
			ArrayList<HashMap<Model3D, ArrayList<Coords>>> models) {
		super();
		this.coords = coords;
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

	public Coords getCoords() {
		return coords;
	}

	public void setCoords(Coords coords) {
		this.coords = coords;
	}

}
