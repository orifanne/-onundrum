package ori.conundrum;

import java.util.HashMap;

/**
 * Представляет игровой объект - единицу, имеющую набор 3d-моделей и координаты.
 * 
 * @author orifanne
 * 
 */
public class GameObject {

	/**
	 * Координаты.
	 */
	protected Coords coords;

	/**
	 * Признак видимости.
	 */
	private boolean isVisible = true;

	/**
	 * Набор 3d-моделей с координатами.
	 */
	private HashMap<Model3D, Coords> models;

	public GameObject(Coords coords, HashMap<Model3D, Coords> models) {
		super();
		this.coords = coords;
		this.models = models;
	}

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

	public HashMap<Model3D, Coords> getModels() {
		return models;
	}

}
