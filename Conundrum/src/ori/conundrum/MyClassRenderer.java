package ori.conundrum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

/**
 * Организует рендеринг
 * 
 * @author orifanne
 * 
 */
public class MyClassRenderer implements GLSurfaceView.Renderer {

	/** Тэг для дебага */
	private static final String TAG = "MyClassRenderer";

	/** Current display width */
	private int mDisplayWidth;
	/** Current display height */
	private int mDisplayHeight;

	/** Координаты источника света */
	private float[] lightPos = { 2.0f, 2.0f, 2.0f, 1, // position
			0.0f, 0.0f, 0.0f, // center (where the light is looking at)
			0.0f, 1.0f, 0.0f, // up vector
	};

	/** RENDER TO TEXTURE VARIABLES */
	int[] fboId;
	int[] depthTextureId;
	int[] renderTextureId;

	/** Current shadow map width */
	private int mShadowMapWidth;
	/** Current shadow map height */
	private int mShadowMapHeight;

	/** Array of shaders */
	Shader _shaders[] = new Shader[4];

	/** Vertex shader codes (files indices) **/
	private int[] vShaders;
	/** Fragment shader codes (files indices) **/
	private int[] fShaders;

	/** Shader ids */
	private final int OLD_SHADER = 0;
	private final int DEPTHMAP_SHADER = 1; // generates the depth map
	private final int SIMPLE_SHADOW_CONSTANT_BIAS_SHADER = 2;
	private final int SIMPLE_SHADOW_DYNAMIC_BIAS_SHADER = 3;

	GameObject plane;
	Ball sphere;

	/** Матрица модели (для камеры и света сразу) */
	private float[] mModelMatrix = new float[16];
	/** Матрица вида для камеры */
	private float[] mViewMatrix = new float[16];
	/** Матрица проекции для камеры */
	private float[] mProjectionMatrix = new float[16];
	/** Матрица модели-вида-проекции для камеры */
	private float[] mMVPMatrix = new float[16];

	/** Матрица вида для света */
	private float[] lViewMatrix = new float[16];
	/** Матрица проекции для света */
	private float[] lProjectionMatrix = new float[16];
	/** Матрица модели-вида-проекции для света */
	private float[] lMVPMatrix = new float[16];

	Model3D model;
	Texture texture;

	Context context;

	/** Cтационарные игровые объекты */
	ArrayList<GameObject> objects;
	/** Мобильные игровые объекты */
	ArrayList<FlexibleGameObject> flexibleObjects;
	/** Набор матриц проекции теней */
	ArrayList<float[]> lMVP;

	public MyClassRenderer(Context context) {
		this.context = context;

		objects = new ArrayList<GameObject>();
		flexibleObjects = new ArrayList<FlexibleGameObject>();

		// setup all the shaders
		vShaders = new int[4];
		fShaders = new int[4];

		// phong shading with simple shadow constant bias
		vShaders[SIMPLE_SHADOW_CONSTANT_BIAS_SHADER] = R.raw.simple_shadow;
		fShaders[SIMPLE_SHADOW_CONSTANT_BIAS_SHADER] = R.raw.simple_shadow_constant_bias_ps;

		// phong shading with simple shadow dynamic bias
		vShaders[SIMPLE_SHADOW_DYNAMIC_BIAS_SHADER] = R.raw.simple_shadow;
		fShaders[SIMPLE_SHADOW_DYNAMIC_BIAS_SHADER] = R.raw.simple_shadow_dynamic_bias_ps;

		// Depth map
		vShaders[DEPTHMAP_SHADER] = R.raw.depth_vs;
		fShaders[DEPTHMAP_SHADER] = R.raw.depth_ps;

		// old shader
		vShaders[OLD_SHADER] = R.raw.old_vs;
		fShaders[OLD_SHADER] = R.raw.old_fs;

	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {

		// Set the background frame color
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		// Enable depth testing
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_CULL_FACE);

		// initialize shaders
		try {
			_shaders[SIMPLE_SHADOW_CONSTANT_BIAS_SHADER] = new Shader(
					vShaders[SIMPLE_SHADOW_CONSTANT_BIAS_SHADER],
					fShaders[SIMPLE_SHADOW_CONSTANT_BIAS_SHADER], context); // gouraud
			_shaders[SIMPLE_SHADOW_DYNAMIC_BIAS_SHADER] = new Shader(
					vShaders[SIMPLE_SHADOW_DYNAMIC_BIAS_SHADER],
					fShaders[SIMPLE_SHADOW_DYNAMIC_BIAS_SHADER], context); // phong
			_shaders[DEPTHMAP_SHADER] = new Shader(vShaders[DEPTHMAP_SHADER],
					fShaders[DEPTHMAP_SHADER], context); // depth map
			_shaders[OLD_SHADER] = new Shader(vShaders[OLD_SHADER],
					fShaders[OLD_SHADER], context); // old
		} catch (Exception e) {
			Log.d("Shader Setup", e.getLocalizedMessage());
		}

		// arrange scene

		texture = new Texture(context, R.drawable.back64);

		// сфера
		model = new Model3D(context, "icosphere.obj", texture);
		sphere = new Ball(new Coords(0, 0, 0), model, new Coords(-2, 1, 0),
				new Coords(2, -1, 0), 0.3f);

		// плоскость
		model = new Model3D(context, "plane.obj", texture);
		plane = new GameObject(new Coords(0, 0, 0f), model);
		
		
		model = new Model3D(context, "cube.obj", texture);
		GameObject cube = new GameObject(new Coords(2f, 2f, 0f), model);
		
		model = new Model3D(context, "icosphere.obj", texture);
		Ball sphere1 = new Ball(new Coords(-1, -1, 0), model, new Coords(-2, 1, 0),
				new Coords(2, -1, 0), 0.3f);
		
		objects.add(plane);
		objects.add(sphere1);
		objects.add(cube);
		
		flexibleObjects.add(sphere);
					
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		mDisplayWidth = width;
		mDisplayHeight = height;
		// Adjust the viewport based on geometry changes,
		// such as screen rotation
		GLES20.glViewport(0, 0, mDisplayWidth, mDisplayHeight);
		// Generate buffer where depth values are saved for shadow calculation
		generateShadowFBO();
		float ratio = (float) mDisplayWidth / mDisplayHeight;
		// this projection matrix is applied at rendering scene
		// in the onDrawFrame() method
		float bottom = -1.0f;
		float top = 1.0f;
		float near = 1.0f;
		float far = 100.0f;
		Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, bottom, top, near,
				far);
		// this projection matrix is used at rendering shadow map
		Matrix.frustumM(lProjectionMatrix, 0, -1.1f * ratio, 1.1f * ratio,
				1.1f * bottom, 1.1f * top, near, far);
	}

	/**
	 * Sets up the framebuffer and renderbuffer to render to texture
	 */
	public void generateShadowFBO() {
		mShadowMapWidth = mDisplayWidth;
		mShadowMapHeight = mDisplayHeight;
		fboId = new int[1];
		depthTextureId = new int[1];
		renderTextureId = new int[1];
		// create a framebuffer object
		GLES20.glGenFramebuffers(1, fboId, 0);
		// create render buffer and bind 16-bit depth buffer
		GLES20.glGenRenderbuffers(1, depthTextureId, 0);
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthTextureId[0]);
		GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER,
				GLES20.GL_DEPTH_COMPONENT16, mShadowMapWidth, mShadowMapHeight);
		// Try to use a texture depth component
		GLES20.glGenTextures(1, renderTextureId, 0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderTextureId[0]);
		// GL_LINEAR does not make sense for depth texture. However, next
		// tutorial shows usage of GL_LINEAR and PCF. Using GL_NEAREST
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		// Remove artifact on the edges of the shadowmap
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId[0]);

		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
				mShadowMapWidth, mShadowMapHeight, 0, GLES20.GL_RGBA,
				GLES20.GL_UNSIGNED_BYTE, null);
		// specify texture as color attachment
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
				GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D,
				renderTextureId[0], 0);
		// attach the texture to FBO depth attachment point
		// (not supported with gl_texture_2d)
		GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER,
				GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER,
				depthTextureId[0]);

		// check FBO status
		int FBOstatus = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
		if (FBOstatus != GLES20.GL_FRAMEBUFFER_COMPLETE) {
			Log.e(TAG, "GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO");
			throw new RuntimeException(
					"GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO");
		}
	}

	/**
	 * Сгенерировать матрицу вида с точки зрения камеры для определенного
	 * шейдера
	 * 
	 * @param _program
	 *            индекс шейдера в массиве
	 */
	private void viewFromCamera(int _program) {

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
		Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY,
				lookZ, upX, upY, upZ);

		GLES20.glUniform3f(_shaders[_program].getCameraHandle(), eyeX, eyeY,
				eyeZ);
	}

	/**
	 * Сгенерировать матрицу вида с точки зрения источника света
	 */
	private void viewFromLight() {

		// View from the light's perspective
		Matrix.setLookAtM(lViewMatrix, 0, lightPos[0], lightPos[1],
				lightPos[2], lightPos[4], lightPos[5], lightPos[6],
				lightPos[7], lightPos[8], lightPos[9]);

	}

	@Override
	public void onDrawFrame(GL10 glUnused) {

		GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

		// ------------------------- render depth map --------------------------
		// Cull front faces for shadow generation to avoid self shadowing
		GLES20.glCullFace(GLES20.GL_FRONT);
		renderShadowMap();
		// ------------------------- render scene ------------------------------
		// Cull back faces for normal render
		GLES20.glCullFace(GLES20.GL_BACK);
		renderScene();
	}

	/**
	 * Отрисовать игровой объект для shadow mapping или финального рендера.
	 * 
	 * @param o
	 *            объект
	 * @param shadowMap
	 *            is shadow mapping
	 * @param _program
	 *            индекс шейдера в массиве _shaders
	 */
	private void drawGameObject(GameObject o, boolean shadowMap, int _program,
			int i) {

		Model3D m = o.getModel();

		// переносим и поворачиваем матрицу модели в соответствии с
		// координатами этого объекта в пространстве

		Matrix.setIdentityM(mModelMatrix, 0);

		Matrix.translateM(mModelMatrix, 0, o.getCoords().getX(), o.getCoords()
				.getY(), o.getCoords().getZ());
		Matrix.rotateM(mModelMatrix, 0, o.getCoords().getXAngle(), 1, 0, 0);
		Matrix.rotateM(mModelMatrix, 0, o.getCoords().getYAngle(), 0, 1, 0);

		// делаем матрицу модели-вида-проекции для отрисовки теней
		if (shadowMap) {
			// View matrix * Model matrix value is stored
			Matrix.multiplyMM(lMVPMatrix, 0, lViewMatrix, 0, mModelMatrix, 0);
			// Model * view * projection matrix stored and copied for use at
			// rendering from camera point of view
			Matrix.multiplyMM(lMVPMatrix, 0, lProjectionMatrix, 0, lMVPMatrix,
					0);
			lMVP.add(lMVPMatrix.clone());
			// Pass in the combined matrix.
			GLES20.glUniformMatrix4fv(_shaders[_program].getLightMVPMatrixHandle(),
					1, false, lMVPMatrix, 0);
			
			// рисуем
			// !!! 
			//m.draw(_shaders[_program]);
		}
		// или для финального рендера и шлем ее в шейдер
		else {
			Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
			Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix,
					0);
			GLES20.glUniformMatrix4fv(_shaders[_program].getMVPMatrixHandle(),
					1, false, mMVPMatrix, 0);

			// send the shadow projection matrix
			GLES20.glUniformMatrix4fv(
				_shaders[_program].getShadowProjMatrixHandle(), 1, false,
					lMVP.get(i), 0);

			// и скорректировать и передать координаты источника света
			float[] light = { lightPos[0], lightPos[1], lightPos[2], 0 };
			float[] inverted = new float[16];
			Matrix.invertM(inverted, 0, mModelMatrix, 0);
			Matrix.multiplyMV(light, 0, inverted, 0, light, 0);
			GLES20.glUniform3f(_shaders[_program].getLightPositionHandle(),
					light[0], light[1], light[2]);
			
			// рисуем
			m.draw(_shaders[_program]);
		}

	}

	/**
	 * Renders shadow map to a texture
	 */
	private void renderShadowMap() {
		// bind the generated framebuffer
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId[0]);
		GLES20.glViewport(0, 0, mShadowMapWidth, mShadowMapHeight);
		// Clear color and buffers
		GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		drawAllObjects(DEPTHMAP_SHADER, true);
	}

	/**
	 * Renders the scene with the shadows (only after renderShadowMap())
	 */
	private void renderScene() {

		int _program = SIMPLE_SHADOW_CONSTANT_BIAS_SHADER;
		//int _program = OLD_SHADER;

		// bind default framebuffer
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

		GLES20.glViewport(0, 0, mDisplayWidth, mDisplayHeight);

		// pass in texture where depth map is stored
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderTextureId[0]);
		GLES20.glUniform1i(_shaders[_program].getShadowTextureHandle(), 0);

		// DRAW ALL THE OBJECTS
		drawAllObjects(_program, false);
	}

	/**
	 * Отрисовывает все объекты сцены для shadow mapping или финального рендера
	 * 
	 * @param _program
	 *            the shader program
	 * @param shadowMap
	 *            is shadow mapping
	 */
	void drawAllObjects(int _program, boolean shadowMap) {
		if (shadowMap) {
			for (int i = 0; i < flexibleObjects.size(); i++)
				flexibleObjects.get(i).countCoords();
			lMVP = new ArrayList<float[]>();
			viewFromLight();
			int i;
			for (i = 0; i < objects.size(); i++) {
				drawGameObject(objects.get(i), shadowMap, _program, i);
			}
			for (int j = 0; j < flexibleObjects.size(); j++) {
				drawGameObject(flexibleObjects.get(j), shadowMap, _program, i + j);
			}
		} else {
			viewFromCamera(_program);
			int i;
			for (i = 0; i < objects.size(); i++) {
				drawGameObject(objects.get(i), shadowMap, _program, i);
			}
			//drawGameObject(objects.get(1), shadowMap, _program, 1);
			//drawGameObject(objects.get(2), shadowMap, _program, 2);
			//drawGameObject(objects.get(0), shadowMap, _program, 0);
			for (int j = 0; j < flexibleObjects.size(); j++) {
				drawGameObject(flexibleObjects.get(j), shadowMap, _program, i + j);
			}
		}
		
	}

}
