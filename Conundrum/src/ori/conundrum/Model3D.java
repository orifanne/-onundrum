package ori.conundrum;

/**
 * Представляет 3d-модель.
 * 
 * @author orifanne
 * 
 */
public class Model3D {

	/**
	 * Массив вершин. Данные о вершинах хранятся как array of structures.
	 * Координаты, цвет, нормаль, текустурные координаты.
	 */
	private float[] vertices;
	/** Массив индексов */
	private byte[] indices;

	private static final int VERTEX_POS_SIZE = 3;
	private static final int VERTEX_NORMAL_SIZE = 3;
	private static final int VERTEX_TEXCOORD_SIZE = 2;

	private static final int VERTEX_POS_INDX = 0;
	private static final int VERTEX_NORMAL_INDX = 1;
	private static final int VERTEX_TEXCOORD_INDX = 2;

	private static final int VERTEX_POS_OFFSET = 0;
	private static final int VERTEX_NORMAL_OFFSET = 3;
	private static final int VERTEX_TEXCOORD_OFFSET = 6;

	private static final int VERTEX_ATTRIB_SIZE = VERTEX_POS_SIZE
			+ VERTEX_NORMAL_SIZE + VERTEX_TEXCOORD_SIZE;

	public Model3D(float[] vertices, byte[] indices) {
		super();
		this.vertices = vertices;
		this.indices = indices;
	}

	/**
	 * Отрисовать модель.
	 */
	public void draw(Coords coords) {

	}

	public float[] getVertices() {
		return vertices;
	}

	public byte[] getIndices() {
		return indices;
	}
}
