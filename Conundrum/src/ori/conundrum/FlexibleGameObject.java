package ori.conundrum;

public abstract class FlexibleGameObject extends GameObject {

	public FlexibleGameObject(Coords coords, Model3D model) {
		super(coords, model);
	}

	/**
	 * Пересчитать координаты центра в соответствии с текущим состоянием
	 */
	public abstract void countCoords();

}
