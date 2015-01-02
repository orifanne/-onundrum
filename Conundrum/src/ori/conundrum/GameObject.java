package ori.conundrum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.util.Log;

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

	/**
	 * Отрисовать объект.
	 */
	/*
	public void draw() {

		Iterator<Model3D> it = models.keySet().iterator();
		while (it.hasNext()) {
			Model3D m = it.next();
			ArrayList<Coords> c = models.get(m);

			for (int i = 0; i < c.size(); i++) {
				Log.d("***************", Float.toString( c.get(i).getX()));
				// транспонирование
				Coords s = new Coords(c.get(i).getX() + coords.getX(), c.get(i)
						.getY() + coords.getY(), c.get(i).getZ()
						+ coords.getZ());
				m.draw(s);
			}

			// it.remove(); // avoids a ConcurrentModificationException
		}

	}
	*/

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
