package ori.conundrum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;

public class Mesh {

	/**
	 * Массив вершин. Данные о вершинах хранятся как array of structures.
	 * Координаты, нормаль, текустурные координаты, цвет.
	 */
	private float[] verticesData;
	
	/**
	 * Упакованные данные о вершинах.
	 */
	FloatBuffer vertices;

	/**
	 * @param context
	 * @param file
	 * @param shader
	 * @param texture
	 */
	public Mesh(Context context, String file) {
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
				if (f.get(i) != null)
					verticesData[i] = f.get(i);
				else
					verticesData[i] = 0;

			this.vertices = ByteBuffer
					.allocateDirect(verticesData.length * (Float.SIZE / 8))
					.order(ByteOrder.nativeOrder()).asFloatBuffer();

			// ?
			this.vertices.put(verticesData).position(0);


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public FloatBuffer getVertices() {
		return vertices;
	}
	
	public int size() {
		return verticesData.length;
	}
}
