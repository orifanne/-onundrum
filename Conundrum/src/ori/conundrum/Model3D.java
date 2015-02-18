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

	/** Координатная сетка */
	Mesh mesh;

	/** Текстура */
	private Texture texture;

	/** Размер (в элементах) инормации о координатах вершины в массиве */
	private static final int VERTEX_POS_SIZE = 3;
	/** Размер (в элементах) инормации о координатах нормали в массиве */
	private static final int VERTEX_NORMAL_SIZE = 3;
	/** Размер (в элементах) инормации о координатах текстуры в массиве */
	private static final int VERTEX_TEXCOORD_SIZE = 2;
	/** Размер (в элементах) инормации о цвете вершины в массиве */
	private static final int VERTEX_COLOR_SIZE = 4;

	/** Размер (в элементах) cмещения инормации о координатах вершины в массиве */
	private static final int VERTEX_POS_OFFSET = 0;
	/** Размер (в элементах) cмещения инормации о координатах нормали в массиве */
	private static final int VERTEX_NORMAL_OFFSET = 3;
	/** Размер (в элементах) cмещения инормации о координатах текстуры в массиве */
	private static final int VERTEX_TEXCOORD_OFFSET = 6;
	/** Размер (в элементах) cмещения инормации о цвете вершины в массиве */
	private static final int VERTEX_COLOR_OFFSET = 8;

	/** Размер (в байтах) инормации о всех атрибутах вершины в массиве */
	private static final int VERTEX_ATTRIB_SIZE = (VERTEX_POS_SIZE
			+ VERTEX_NORMAL_SIZE + VERTEX_TEXCOORD_SIZE + VERTEX_COLOR_SIZE)
			* (Float.SIZE / 8);

	/** Размер (в элементах) инормации о всех атрибутах вершины в массиве */
	private static final int SIZE = VERTEX_POS_SIZE + VERTEX_NORMAL_SIZE
			+ VERTEX_TEXCOORD_SIZE + VERTEX_COLOR_SIZE;

	/**
	 * @param context
	 *            контекст
	 * @param file
	 *            obj файл с 3d сеткой
	 * @param shader
	 *            шейдер
	 * @param texture
	 *            текстура
	 */
	public Model3D(Context context, String file, Texture texture) {

		mesh = new Mesh(context, file);

		this.texture = texture;
	}

	/**
	 * Связывает буфер координат вершин vertices с атрибутами в шейдере.
	 * 
	 * @param shader
	 *            шейдер
	 */
	public void linkVertexBuffer(Shader shader) {

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
	 * Отрисовать модель определенным шейдером
	 * 
	 * @param shader
	 *            шейдер
	 */
	public void draw(Shader shader) {

		linkVertexBuffer(shader);
		// shader.linkTexture(texture);

		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mesh.size() / SIZE);
		
		// was it really important?
		//unlinkVertexBuffer(shader);
	}

	/**
	 * Удаляет связи между буфером координат вершин vertices и атрибутами в
	 * шейдере
	 * 
	 * @param shader
	 *            шейдер
	 */
	private void unlinkVertexBuffer(Shader shader) {
		GLES20.glDisableVertexAttribArray(shader.getPositionHandle());
		GLES20.glDisableVertexAttribArray(shader.getNormalHandle());
		GLES20.glDisableVertexAttribArray(shader.getTextureHandle());
		GLES20.glDisableVertexAttribArray(shader.getColorHandle());
	}

	/**
	 * @param texture
	 *            текстура
	 */
	public void setTexture(Texture texture) {
		this.texture = texture;
	}

}
