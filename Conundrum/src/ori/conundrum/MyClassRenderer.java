package ori.conundrum;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Iterator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

public class MyClassRenderer implements GLSurfaceView.Renderer {

	/** Ширина и высота экрана */
	int w, h;

	private static String TAG = "Renderer";

	private static final int FLOAT_SIZE_BYTES = 4;

	// light parameters
	private float[] lightPos = { 17.0f, 20.0f, 10.0f, 1, // position
			0.0f, 0.0f, 0.0f, // center (where the light is looking at)
			0.0f, 1.0f, 0.0f, // up vector
	};

	// RENDER TO TEXTURE VARIABLES
	int[] fb, depthRb, renderTex;
	final int texW = 512;// 480;
	final int texH = 512;// 800;
	IntBuffer texBuffer;

	// viewport variables
	float ratio = 1.0f;

	// array of shaders
	Shader _shaders[] = new Shader[3];
	private int _currentShader;

	/** Shader code **/
	private int[] vShaders;
	private int[] fShaders;

	// shader constants
	private final int GOURAUD_SHADER = 0;
	private final int PHONG_SHADER = 1;
	private final int DEPTHMAP_SHADER = 2; // generates the depth map
	private final int OLD_SHADER = 2;

	GameObject plane;
	Ball sphere;

	Coords globalLightPos = new Coords(2, 2, 2);

	private float[] mModelMatrix = new float[16];
	private float[] mViewMatrix = new float[16];
	private float[] mProjectionMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];

	// Matrices for the light
	private float[] lMVPMatrix = new float[16];
	private float[] lProjMatrix = new float[16];
	private float[] lViewMatrix = new float[16];

	Model3D model;
	Shader shader;
	Texture texture;

	Context context;

	/**
	 * Initialize the model data.
	 */
	public MyClassRenderer(Context context) {
		this.context = context;

		// setup all the shaders
		vShaders = new int[3];
		fShaders = new int[3];

		// basic - just gouraud shading
		vShaders[GOURAUD_SHADER] = R.raw.gouraud_vs;
		fShaders[GOURAUD_SHADER] = R.raw.gouraud_ps;

		// phong shading
		vShaders[PHONG_SHADER] = R.raw.phong_vs;
		fShaders[PHONG_SHADER] = R.raw.phong_ps;

		// Depth map
		vShaders[DEPTHMAP_SHADER] = R.raw.depth_vs;
		fShaders[DEPTHMAP_SHADER] = R.raw.depth_ps;

		// old shader
		vShaders[OLD_SHADER] = R.raw.old_vs;
		fShaders[OLD_SHADER] = R.raw.old_fs;

	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {

		// initialize shaders
		try {
			_shaders[GOURAUD_SHADER] = new Shader(vShaders[GOURAUD_SHADER],
					fShaders[GOURAUD_SHADER], context); // gouraud
			_shaders[PHONG_SHADER] = new Shader(vShaders[PHONG_SHADER],
					fShaders[PHONG_SHADER], context); // phong
			_shaders[DEPTHMAP_SHADER] = new Shader(vShaders[DEPTHMAP_SHADER],
					fShaders[DEPTHMAP_SHADER], context); // depth map
			_shaders[OLD_SHADER] = new Shader(vShaders[OLD_SHADER],
					fShaders[OLD_SHADER], context); // old
		} catch (Exception e) {
			Log.d("Shader Setup", e.getLocalizedMessage());
		}

		// Setup Render to texture
		setupRenderToTexture();

		// Set the background clear color to gray.
		GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		shader = _shaders[OLD_SHADER];
		texture = new Texture(context, R.drawable.back64);

		// сфера
		model = new Model3D(context, "icosphere.obj", shader, texture);
		HashMap<Model3D, Coords> h2 = new HashMap<Model3D, Coords>();
		Coords c2 = new Coords(0, 0, 0);
		h2.put(model, c2);
		sphere = new Ball(new Coords(0, 0, 0), h2, new Coords(-2, 1, 0),
				new Coords(2, -1, 0.2f), 0.3f);

		// плоскость
		model = new Model3D(context, "plane.obj", shader, texture);
		HashMap<Model3D, Coords> h1 = new HashMap<Model3D, Coords>();
		Coords c1 = new Coords(0, 0, 0);
		h1.put(model, c1);
		plane = new GameObject(new Coords(0, 0, 0f), h1);
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		// Set the OpenGL viewport to the same size as the surface.
		GLES20.glViewport(0, 0, width, height);
		w = width;
		h = height;
		// Create a new perspective projection matrix. The height will stay the
		// same
		// while the width will vary as per aspect ratio.
		final float ratio = (float) width / height;
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 1.0f;
		final float far = 10.0f;
		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near,
				far);
	}

	/**
	 * Сгенерировать матрицу вида с точки зрения камеры
	 * 
	 * @param viewMatrix
	 *            матрица вида
	 * @param _program
	 *            индекс шейдера в массиве
	 */
	private void viewFromCamera(float[] viewMatrix, int _program) {

		GLES20.glViewport(0, 0, w, h);

		// Максимальный угол наклона камеры (рад)
		final float angle = (float) 0.25;

		// насколько камера удалена от центра координат
		final float lookDistance = 3f;

		// Position the eye behind the origin.
		final float eyeX = (float) Math.cos(Math.PI
				* 0.5
				+ (MainActivity.xAngle / Math.abs(MainActivity.xAngle))
				* Math.min(Math.abs(Math.min(Math.abs(MainActivity.xAngle),
						Math.PI - Math.abs(MainActivity.xAngle))), angle))
				* lookDistance;
		final float eyeY = (float) Math.cos(Math.PI * 0.5
				+ (MainActivity.yAngle / Math.abs(MainActivity.yAngle))
				* Math.min(Math.abs(MainActivity.yAngle), angle))
				* lookDistance;
		final float eyeZ = (float) Math.sqrt(lookDistance * lookDistance
				- (eyeX * eyeX + eyeY * eyeY));
		// We are looking toward the distance
		final float lookX = 0.0f;
		final float lookY = 0.0f;
		final float lookZ = -5.0f;
		// Set our up vector. This is where our head would be pointing were we
		// holding the camera.
		final float upX = 0.0f;
		final float upY = 1.0f;
		final float upZ = 0.0f;
		// Set the view matrix. This matrix can be said to represent the camera
		// position.
		// NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination
		// of a model and
		// view matrix. In OpenGL 2, we can keep track of these matrices
		// separately if we choose.
		Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ,
				upX, upY, upZ);

		GLES20.glUniform3f(_shaders[_program].getCameraHandle(), eyeX, eyeY,
				eyeZ);
	}

	/**
	 * Сгенерировать матрицу вида с точки зрения источника света
	 * 
	 * @param viewMatrix
	 *            матрица вида
	 */
	private void viewFromLight(float[] viewMatrix) {

		GLES20.glViewport(0, 0, texW, texH);

		// View from the light's perspective
		Matrix.setLookAtM(lViewMatrix, 0, lightPos[0], lightPos[1],
				lightPos[2], lightPos[4], lightPos[5], lightPos[6],
				lightPos[7], lightPos[8], lightPos[9]);
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {

		viewFromCamera(mViewMatrix, OLD_SHADER);

		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

		sphere.countCoords();
		drawGameObject(sphere, mViewMatrix, OLD_SHADER);
		drawGameObject(plane, mViewMatrix, OLD_SHADER);
	}

	/**
	 * Отрисовать игровой объект.
	 * 
	 * @param o
	 *            объект
	 * @param viewMatrix
	 *            матрица вида
	 * @param _program
	 *            индекс шейдера в массиве _shaders
	 */
	private void drawGameObject(GameObject o, float[] viewMatrix, int _program) {
		Iterator<Model3D> it = o.getModels().keySet().iterator();
		while (it.hasNext()) {
			Model3D m = it.next();
			Coords c = o.getModels().get(m);

			Matrix.setIdentityM(mModelMatrix, 0);

			Matrix.translateM(mModelMatrix, 0, o.getCoords().getX(), o
					.getCoords().getY(), c.getZ());
			Matrix.rotateM(mModelMatrix, 0, o.getCoords().getXAngle(), 1, 0, 0);
			Matrix.rotateM(mModelMatrix, 0, o.getCoords().getYAngle(), 0, 1, 0);

			Matrix.translateM(mModelMatrix, 0, c.getX(), c.getY(), c.getZ());
			Matrix.rotateM(mModelMatrix, 0, c.getXAngle(), 1, 0, 0);
			Matrix.rotateM(mModelMatrix, 0, c.getYAngle(), 0, 1, 0);

			Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, mModelMatrix, 0);
			Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix,
					0);
			GLES20.glUniformMatrix4fv(_shaders[_program].getMVPMatrixHandle(),
					1, false, mMVPMatrix, 0);

			float[] light = { globalLightPos.getX(), globalLightPos.getY(),
					globalLightPos.getZ(), 0 };
			float[] inverted = new float[16];
			Matrix.invertM(inverted, 0, mModelMatrix, 0);
			Matrix.multiplyMV(light, 0, inverted, 0, light, 0);
			GLES20.glUniform3f(_shaders[_program].getLightPositionHandle(),
					light[0], light[1], light[2]);

			// !
			m.setShader(_shaders[_program]);
			m.draw();

		}
	}

	/**
	 * Renders to a texture
	 */
	private boolean renderDepthToTexture() {
		// Cull front faces for shadow generation
		GLES20.glCullFace(GLES20.GL_FRONT);

		// bind the generated framebuffer
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fb[0]);

		// specify texture as color attachment
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
				GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D,
				renderTex[0], 0);

		// attach render buffer as depth buffer
		GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER,
				GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, depthRb[0]);

		// check status
		int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
		if (status != GLES20.GL_FRAMEBUFFER_COMPLETE)
			return false;

		/*** DRAW ***/
		// Clear color and buffers
		GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

		viewFromLight(lViewMatrix);

		// DRAW ALL THE OBJECTS
		drawAllObjects(lViewMatrix, DEPTHMAP_SHADER);

		/****
		 * render with shadow now -- Steps: -Render the scene as usual -Pass in
		 * depth map -Project the depth map onto the scene -Compare depth values
		 * to see if pixel is visible (in shadow or not?)
		 */
		// renderWithShadow(lMVPMatrix);

		/** END DRAWING OBJECT ***/
		return true;
	}

	/**
	 * Sets up the framebuffer and renderbuffer to render to texture
	 */
	private void setupRenderToTexture() {
		fb = new int[1];
		depthRb = new int[1];
		renderTex = new int[1];

		// generate
		GLES20.glGenFramebuffers(1, fb, 0);
		GLES20.glGenRenderbuffers(1, depthRb, 0);
		GLES20.glGenTextures(1, renderTex, 0);

		// generate color texture
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderTex[0]);

		// parameters
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);

		// create it
		// create an empty intbuffer first?
		int[] buf = new int[texW * texH];
		texBuffer = ByteBuffer.allocateDirect(buf.length * FLOAT_SIZE_BYTES)
				.order(ByteOrder.nativeOrder()).asIntBuffer();
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, texW,
				texH, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, texBuffer);

		// create render buffer and bind 16-bit depth buffer
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRb[0]);
		GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER,
				GLES20.GL_DEPTH_COMPONENT16, texW, texH);
	}

	/**
	 * Draws all the objects to draw
	 * 
	 * @param _program
	 *            the shader program
	 */
	void drawAllObjects(float[] viewMatrix, int _program) {
		sphere.countCoords();
		drawGameObject(sphere, viewMatrix, _program);
		drawGameObject(plane, viewMatrix, _program);
	}

}
