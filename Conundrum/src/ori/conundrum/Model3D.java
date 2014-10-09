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
	 * Координаты, цвет, нормаль, текустурные координаты.
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

	private static final int VERTEX_POS_OFFSET = 0;
	private static final int VERTEX_NORMAL_OFFSET = 3;
	private static final int VERTEX_TEXCOORD_OFFSET = 6;

	private static final int VERTEX_ATTRIB_SIZE = (VERTEX_POS_SIZE
			+ VERTEX_NORMAL_SIZE + VERTEX_TEXCOORD_SIZE)
			* 4;

	public Model3D(float[] vertices, byte[] indices) {
		super();
		this.verticesData = vertices;
		this.indicesData = indices;
	}

	public Model3D(float[] vertices, byte[] indices, Shader shader,
			Texture texture) {
		super();
		this.verticesData = vertices;
		this.indicesData = indices;
		this.shader = shader;
		this.texture = texture;
		
		this.vertices = ByteBuffer
				.allocateDirect(verticesData.length * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();

		this.indices = ByteBuffer.allocateDirect(
				indicesData.length).order(
				ByteOrder.nativeOrder());
		
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
	}

	/**
	 * Отрисовать модель.
	 */
	public void draw(Coords coords) {
		// GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesData.length,
		// GLES20.GL_UNSIGNED_BYTE, indices);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 1);
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
