package ori.conundrum;

import android.content.Context;
import android.opengl.GLES20;

/**
 * Представляет 3d-модель.
 * 
 * @author orifanne
 * 
 */
public class Model3D {

	/**
	 * Координатная сетка.
	 */
	Mesh mesh;

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

	private static final int SIZE = VERTEX_POS_SIZE + VERTEX_NORMAL_SIZE
			+ VERTEX_TEXCOORD_SIZE + VERTEX_COLOR_SIZE;

	/**
	 * @param context
	 * @param file
	 * @param shader
	 * @param texture
	 */
	public Model3D(Context context, String file, Shader shader, Texture texture) {

		mesh = new Mesh(context, file);
		
		this.shader = shader;
		this.texture = texture;
	}

	/**
	 * Связывает буфер координат вершин vertices с атрибутами в шейдере.
	 */
	public void linkVertexBuffer() {

		// устанавливаем активную программу
		GLES20.glUseProgram(shader.getProgramHandle());

		mesh.getVertices().position(VERTEX_POS_OFFSET);
		// связываем буфер координат вершин vertices с атрибутом a_Position
		GLES20.glVertexAttribPointer(shader.getPositionHandle(),
				VERTEX_POS_SIZE, GLES20.GL_FLOAT, false, VERTEX_ATTRIB_SIZE,
				mesh.getVertices());
		// включаем использование атрибута a_Position
		GLES20.glEnableVertexAttribArray(shader.getPositionHandle());

		mesh.getVertices().position(VERTEX_NORMAL_OFFSET);
		// связываем буфер координат вершин vertices с атрибутом a_Normal
		GLES20.glVertexAttribPointer(shader.getNormalHandle(),
				VERTEX_NORMAL_SIZE, GLES20.GL_FLOAT, false, VERTEX_ATTRIB_SIZE,
				mesh.getVertices());
		// включаем использование атрибута a_Normal
		GLES20.glEnableVertexAttribArray(shader.getNormalHandle());

		mesh.getVertices().position(VERTEX_TEXCOORD_OFFSET);
		// связываем буфер координат вершин vertices с атрибутом a_Texture
		GLES20.glVertexAttribPointer(shader.getTextureHandle(),
				VERTEX_TEXCOORD_SIZE, GLES20.GL_FLOAT, false,
				VERTEX_ATTRIB_SIZE, mesh.getVertices());
		// включаем использование атрибута a_Texture
		GLES20.glEnableVertexAttribArray(shader.getTextureHandle());

		mesh.getVertices().position(VERTEX_COLOR_OFFSET);
		// связываем буфер координат вершин vertices с атрибутом a_Color
		GLES20.glVertexAttribPointer(shader.getColorHandle(),
				VERTEX_COLOR_SIZE, GLES20.GL_FLOAT, false, VERTEX_ATTRIB_SIZE,
				mesh.getVertices());
		// включаем использование атрибута a_Color
		GLES20.glEnableVertexAttribArray(shader.getColorHandle());
	}

	/**
	 * Отрисовать модель.
	 */
	public void draw() {
		
		linkVertexBuffer();
		shader.linkTexture(texture);

		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mesh.size() / SIZE);
		unlinkVertexBuffer();
	}

	private void unlinkVertexBuffer() {
		GLES20.glDisableVertexAttribArray(shader.getPositionHandle());
		GLES20.glDisableVertexAttribArray(shader.getNormalHandle());
		GLES20.glDisableVertexAttribArray(shader.getTextureHandle());
		GLES20.glDisableVertexAttribArray(shader.getColorHandle());
	}

	public void setShader(Shader shader) {
		this.shader = shader;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}
}
