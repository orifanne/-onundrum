package ori.conundrum;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

/**
 * Представляет 3d-модель.
 * 
 * @author orifanne
 * 
 */
public class Model3D {

	/**
	 * Массив вершин. Данные о вершинах хранятся как array of structures.
	 * Координаты, нормаль, текустурные координаты, цвет.
	 */
	private float[] verticesData;
	FloatBuffer vertices;

	/** Массив индексов */
	private byte[] indicesData;
	ByteBuffer indices;

	private Shader shader;
	private Texture texture;

	private static final int VERTEX_POS_SIZE = 3;
	private static final int VERTEX_NORMAL_SIZE = 3;
	private static final int VERTEX_TEXCOORD_SIZE = 2;
	private static final int VERTEX_COLOR_SIZE = 4;

	private static final int VERTEX_POS_OFFSET = 0;
	private static final int VERTEX_NORMAL_OFFSET = 3;
	private static final int VERTEX_TEXCOORD_OFFSET = 6;
	private static final int VERTEX_COLOR_OFFSET = 8;

	private static final int VERTEX_ATTRIB_SIZE = (VERTEX_POS_SIZE
			+ VERTEX_NORMAL_SIZE + VERTEX_TEXCOORD_SIZE + VERTEX_COLOR_SIZE)
			* (Float.SIZE / 8);

	public Model3D(float[] vertices, byte[] indices, Shader shader,
			Texture texture) {
		super();
		this.verticesData = vertices;
		this.indicesData = indices;
		this.shader = shader;
		this.texture = texture;

		this.vertices = ByteBuffer
				.allocateDirect(verticesData.length * (Float.SIZE / 8))
				.order(ByteOrder.nativeOrder()).asFloatBuffer();

		this.vertices.put(verticesData).position(0);

		this.indices = ByteBuffer.allocateDirect(
				indicesData.length * (Byte.SIZE / 8)).order(
				ByteOrder.nativeOrder());

		this.indices.put(indicesData).position(0);

		linkVertexBuffer();
		shader.linkTexture(texture);
	}

	/**
	 * Связывает буфер координат вершин vertices с атрибутами в шейдере.
	 */
	public void linkVertexBuffer() {

		// устанавливаем активную программу
		GLES20.glUseProgram(shader.getProgramHandle());

		vertices.position(VERTEX_POS_OFFSET);
		// связываем буфер координат вершин vertices с атрибутом a_Position
		GLES20.glVertexAttribPointer(shader.getPositionHandle(),
				VERTEX_POS_SIZE, GLES20.GL_FLOAT, false, VERTEX_ATTRIB_SIZE,
				vertices);
		// включаем использование атрибута a_Position
		GLES20.glEnableVertexAttribArray(shader.getPositionHandle());

		vertices.position(VERTEX_NORMAL_OFFSET);
		// связываем буфер координат вершин vertices с атрибутом a_Normal
		GLES20.glVertexAttribPointer(shader.getNormalHandle(),
				VERTEX_NORMAL_SIZE, GLES20.GL_FLOAT, false, VERTEX_ATTRIB_SIZE,
				vertices);
		// включаем использование атрибута a_Normal
		GLES20.glEnableVertexAttribArray(shader.getNormalHandle());

		vertices.position(VERTEX_TEXCOORD_OFFSET);
		// связываем буфер координат вершин vertices с атрибутом a_Texture
		GLES20.glVertexAttribPointer(shader.getTextureHandle(),
				VERTEX_TEXCOORD_SIZE, GLES20.GL_FLOAT, false,
				VERTEX_ATTRIB_SIZE, vertices);
		// включаем использование атрибута a_Texture
		GLES20.glEnableVertexAttribArray(shader.getTextureHandle());
		
		vertices.position(VERTEX_COLOR_OFFSET);
		// связываем буфер координат вершин vertices с атрибутом a_Color
		GLES20.glVertexAttribPointer(shader.getColorHandle(),
				VERTEX_COLOR_SIZE, GLES20.GL_FLOAT, false,
				VERTEX_ATTRIB_SIZE, vertices);
		// включаем использование атрибута a_Color
		GLES20.glEnableVertexAttribArray(shader.getColorHandle());
	}

	/**
	 * Отрисовать модель.
	 */
	public void draw(Coords coords) {
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesData.length,
				GLES20.GL_UNSIGNED_BYTE, indices);
	}

	public float[] getVerticesData() {
		return verticesData;
	}

	public byte[] getIndices() {
		return indicesData;
	}

	public void setShader(Shader shader) {
		this.shader = shader;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}
}
