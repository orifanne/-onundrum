package ori.conundrum;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.os.SystemClock;

public class MyClassRenderer implements GLSurfaceView.Renderer {
	/**
	 * Store the model matrix. This matrix is used to move models from object
	 * space (where each model can be thought of being located at the center of
	 * the universe) to world space.
	 */
	private float[] mModelMatrix = new float[16];
	/**
	 * Store the view matrix. This can be thought of as our camera. This matrix
	 * transforms world space to eye space; it positions things relative to our
	 * eye.
	 */
	private float[] mViewMatrix = new float[16];
	/**
	 * Store the projection matrix. This is used to project the scene onto a 2D
	 * viewport.
	 */
	private float[] mProjectionMatrix = new float[16];
	/**
	 * Allocate storage for the final combined matrix. This will be passed into
	 * the shader program.
	 */
	private float[] mMVPMatrix = new float[16];

	private final float lookDistance = 1.5f;

	Model3D model;
	Shader shader;
	Texture texture;

	Context context;

	/**
	 * Initialize the model data.
	 */
	public MyClassRenderer(Context context) {
		this.context = context;
	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		// Set the background clear color to gray.
		GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

		final String vertexShader = "uniform mat4 u_MVPMatrix; \n" // A constant
																	// representing
																	// the
																	// combined
																	// model/view/projection
																	// matrix.
				+ "attribute vec4 a_Position; \n" // Per-vertex position
													// information we will pass
													// in.
				+ "attribute vec2 a_Texture; \n"
				+ "varying vec2 v_Texture; \n"
				+ "attribute vec3 a_Normal; \n"
				+ "varying vec3 v_Normal; \n"
				+ "attribute vec4 a_Color; \n"
				+ "varying vec4 v_Color; \n"
				+ "void main() \n" // The entry point for our vertex shader.
				+ "{ \n"
				// It will be interpolated across the triangle.
				+ " gl_Position = u_MVPMatrix" // gl_Position is a special
													// variable used to store
													// the final position.
				+ " * a_Position; \n" // Multiply the vertex by the matrix to
										// get the final point in
				+ "v_Texture = a_Texture; \n" + "} \n"; // normalized screen
														// coordinates.
		final String fragmentShader = "precision mediump float; \n" // Set the
																	// default
																	// precision
																	// to
																	// medium.
																	// We don't
																	// need as
																	// high of a
				// precision in the fragment shader.
				+ "varying vec2 v_Texture; \n"
				+ "uniform sampler2D u_Texture;\n"
				+ "void main() \n" // The entry point for our fragment shader.
				+ "{ \n"
				+ "vec4 textureColor = texture2D(u_Texture, v_Texture); \n"

				+ "gl_FragColor = textureColor; \n" // Pass the color
														// directly through
														// the pipeline.
				+ "} \n";

		shader = new Shader(vertexShader, fragmentShader);
		texture = new Texture(context, R.drawable.back);

		float[] v = { 0.0f, 0.0f, 0.0f, 
				      0.0f, 0.0f, 1.0f, 
				      0.0f, 0.0f, 
				      1.0f, 1.0f, 1.0f, 1.0f,
				      
				      1.0f, 0.0f, 0.0f, 
				      0.0f, 0.0f, 1.0f, 
				      1.0f, 0.0f, 
				      1.0f, 1.0f, 1.0f, 1.0f,
				      
				      1.0f, 1.0f, 0.0f, 
				      0.0f, 0.0f, 1.0f, 
				      1.0f, 1.0f,
				      1.0f, 1.0f, 1.0f, 1.0f};

		byte[] i = { 2, 1, 0 };

		model = new Model3D(v, i, shader, texture);
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		// Set the OpenGL viewport to the same size as the surface.
		GLES20.glViewport(0, 0, width, height);
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

	@Override
	public void onDrawFrame(GL10 glUnused) {

		// Position the eye behind the origin.
		final float eyeX = (float) Math.cos(Math.PI * 0.5
				- MainActivity.rotationCurrent[2])
				* lookDistance;
		final float eyeY = (float) Math.cos(Math.PI * 0.5
				- MainActivity.rotationCurrent[1])
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

		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

		Matrix.setIdentityM(mModelMatrix, 0);

		// This multiplies the view matrix by the model matrix, and stores the
		// result in the MVP matrix
		// (which currently contains model * view).
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
		// This multiplies the modelview matrix by the projection matrix, and
		// stores the result in the MVP matrix
		// (which now contains model * view * projection).
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
		GLES20.glUniformMatrix4fv(shader.getMVPMatrixHandle(), 1, false,
				mMVPMatrix, 0);

		model.draw(new Coords(0, 0, 0));
	}
}
