package ori.conundrum;

import java.util.HashMap;

/**
 * Представляет игровой объект - единицу, имеющую набор 3d-моделей и координаты.
 * 
 * @author orifanne
 * 
 */
public class GameObject {

	/** Координаты центра */
	protected Coords coords;

	/** Набор 3d-моделей с координатами */
	private HashMap<Model3D, Coords> models;

	/**
	 * @param coords
	 *            координаты центра
	 * @param models
	 *            набор 3d-моделей с координатами
	 */
	public GameObject(Coords coords, HashMap<Model3D, Coords> models) {
		super();
		this.coords = coords;
		this.models = models;
	}

	/**
	 * @return координаты центра
	 */
	public Coords getCoords() {
		return coords;
	}

	/**
	 * @param coords
	 *            координаты центра
	 */
	public void setCoords(Coords coords) {
		this.coords = coords;
	}

	/**
	 * @return набор 3d-моделей с координатами
	 */
	public HashMap<Model3D, Coords> getModels() {
		return models;
	}

}
