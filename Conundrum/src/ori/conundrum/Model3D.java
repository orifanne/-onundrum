package ori.conundrum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.util.Log;

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
	private float[] verticesToDraw;
	FloatBuffer vertices;

	/** Массив индексов */
	// private byte[] indicesData;
	// ByteBuffer indices;

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
		this.verticesData = vertices;
		// this.indicesData = indices;
		this.shader = shader;
		this.texture = texture;

		this.vertices = ByteBuffer
				.allocateDirect(verticesData.length * (Float.SIZE / 8))
				.order(ByteOrder.nativeOrder()).asFloatBuffer();

		// ?
		this.vertices.put(verticesData).position(0);

		/*
		 * this.indices = ByteBuffer.allocateDirect( indicesData.length *
		 * (Byte.SIZE / 8)).order( ByteOrder.nativeOrder());
		 * 
		 * this.indices.put(indicesData).position(0);
		 */

		// ?
		linkVertexBuffer();
		// ?
		shader.linkTexture(texture);
	}

	/**
	 * @param context
	 * @param file
	 * @param shader
	 * @param texture
	 */
	public Model3D(Context context, String file, Shader shader, Texture texture) {
		try {
			AssetManager assetManager = context.getAssets();
			InputStreamReader istream = new InputStreamReader(
					assetManager.open(file));
			BufferedReader reader = new BufferedReader(istream);
			String line;
			ArrayList<Coords> v = new ArrayList<Coords>();
			ArrayList<Coords> vn = new ArrayList<Coords>();
			ArrayList<Float> f = new ArrayList<Float>();
			while ((line = reader.readLine()) != null) {
				// вершина
				if (line.startsWith("v ")) {
					String[] s = line.split(" ");
					v.add(new Coords(Float.parseFloat(s[1]), Float
							.parseFloat(s[2]), Float.parseFloat(s[3])));
				}
				// нормаль
				if (line.startsWith("vn ")) {
					String[] s = line.split(" ");
					vn.add(new Coords(Float.parseFloat(s[1]), Float
							.parseFloat(s[2]), Float.parseFloat(s[3])));
				}
				// грань
				if (line.startsWith("f ")) {
					String[] s = line.split(" ");
					for (int j = 1; j < s.length; j++) {
						String[] s1 = s[j].split("/");
						int vert = Integer.parseInt(s1[0]) - 1;
						int norm = Integer.parseInt(s1[2]) - 1;
						// coords
						f.add(v.get(vert).getX());
						f.add(v.get(vert).getY());
						f.add(v.get(vert).getZ());
						// normal
						f.add(vn.get(norm).getX());
						f.add(vn.get(norm).getY());
						f.add(vn.get(norm).getZ());
						// tex coords
						f.add(0.0f);
						f.add(0.0f);
						// color
						f.add(1.0f);
						f.add(0.0f);
						f.add(0.0f);
						f.add(1.0f);
					}
				}
			}
			verticesData = new float[f.size()];
			for (int i = 0; i < f.size(); i++)
				if (f.get(i) != null) {
					verticesData[i] = f.get(i);
					// Log.d("*****************",
					// Float.toString(verticesData[i]));
				} else
					verticesData[i] = 0;
			this.shader = shader;
			this.texture = texture;
			// Log.d("*****************", Integer.toString(f.size()));

			this.vertices = ByteBuffer
					.allocateDirect(verticesData.length * (Float.SIZE / 8))
					.order(ByteOrder.nativeOrder()).asFloatBuffer();

			// ?
			this.vertices.put(verticesData).position(0);

			// ?
			linkVertexBuffer();
			// ?
			shader.linkTexture(texture);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
				VERTEX_COLOR_SIZE, GLES20.GL_FLOAT, false, VERTEX_ATTRIB_SIZE,
				vertices);
		// включаем использование атрибута a_Color
		GLES20.glEnableVertexAttribArray(shader.getColorHandle());
	}

	/**
	 * Отрисовать модель.
	 */
	public void draw(Coords coords) {
		int size = VERTEX_POS_SIZE + VERTEX_NORMAL_SIZE + VERTEX_TEXCOORD_SIZE
				+ VERTEX_COLOR_SIZE;
		// транспонирование

		verticesToDraw = new float[verticesData.length];
		for (int i = 0; i < verticesData.length; i++) {
			int d = i % size;
			switch (d) {
			case 0:
				verticesToDraw[i] = verticesData[i] + coords.getX();
				break;
			case 1:
				verticesToDraw[i] = verticesData[i] + coords.getY();
				break;
			case 2:
				verticesToDraw[i] = verticesData[i] + coords.getZ();
				break;
			default:
				verticesToDraw[i] = verticesData[i];
			}
		}
		this.vertices = ByteBuffer
				.allocateDirect(verticesData.length * (Float.SIZE / 8))
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		this.vertices.put(verticesToDraw).position(0);
		linkVertexBuffer();
		shader.linkTexture(texture);

		// GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesData.length,
		// GLES20.GL_UNSIGNED_BYTE, indices);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, verticesData.length / size);
		unlinkVertexBuffer();
	}

	/**
	 * Отрисовать модель.
	 */
	public void draw() {
		int size = VERTEX_POS_SIZE + VERTEX_NORMAL_SIZE + VERTEX_TEXCOORD_SIZE
				+ VERTEX_COLOR_SIZE;
		this.vertices = ByteBuffer
				.allocateDirect(verticesData.length * (Float.SIZE / 8))
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		this.vertices.put(verticesData).position(0);
		linkVertexBuffer();
		shader.linkTexture(texture);

		// GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesData.length,
		// GLES20.GL_UNSIGNED_BYTE, indices);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, verticesData.length / size);
		unlinkVertexBuffer();
	}
	
	private void unlinkVertexBuffer() {
		GLES20.glDisableVertexAttribArray(shader.getPositionHandle());
		GLES20.glDisableVertexAttribArray(shader.getNormalHandle());
		GLES20.glDisableVertexAttribArray(shader.getTextureHandle());
		GLES20.glDisableVertexAttribArray(shader.getColorHandle());
	}

	public float[] getVerticesData() {
		return verticesData;
	}

	/*
	 * public byte[] getIndices() { return indicesData; }
	 */

	public void setShader(Shader shader) {
		this.shader = shader;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}
}
