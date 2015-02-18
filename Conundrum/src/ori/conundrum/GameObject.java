package ori.conundrum;

/**
 * Представляет абстракцию игрового объекта - единицы, имеющей набор 3d-моделей
 * и координаты.
 * 
 * @author orifanne
 * 
 */
public class GameObject {

	/** Координаты центра */
	protected Coords coords;

	/** 3d-модель */
	private Model3D model;

	/**
	 * @param coords
	 *            координаты центра
	 * @param model
	 *            3d-модель
	 */
	public GameObject(Coords coords, Model3D model) {
		super();
		this.coords = coords;
		this.model = model;
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
	 * @return 3d-модель
	 */
	public Model3D getModel() {
		return model;
	}

}
