package ori.conundrum;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

public class Shader {

	/** Ссылка на шейдерную программу */
	private int programHandle;

	/** Ссылка на униформу матрицы модели-вида-проекции */
	private int MVPMatrixHandle;

	/** Ссылка на униформу матрицы проекции теней */
	private int shodowProjMatrixHandle;

	/** Ссылка на атрибут координат */
	private int positionHandle;

	/** Ссылка на атрибут нормали */
	private int normalHandle;

	/** Ссылка на атрибут координат текстуры */
	private int textureHandle;

	/** Ссылка на атрибут координат цвета */
	private int colorHandle;

	/** Ссылка на униформу координат камеры */
	private int cameraHandle;

	/** Ссылка на униформу координат источника света */
	private int lightPositionHandle;

	/**
	 * @param vertexShaderCode
	 *            строка-код вершинного шейдера
	 * @param fragmentShaderCode
	 *            строка-код фрагментного шейдера
	 */
	public Shader(String vertexShaderCode, String fragmentShaderCode) {
		// вызываем метод, создающий шейдерную программу
		// при этом заполняется поле program_Handle
		createProgram(vertexShaderCode, fragmentShaderCode);
	}

	/**
	 * Создает шейдерную программу.
	 * 
	 * @param vertexShaderCode
	 *            строка-код вершинного шейдера
	 * @param fragmentShaderCode
	 *            строка-код фрагментного шейдера
	 */
	private void createProgram(String vertexShaderCode,
			String fragmentShaderCode) {
		// получаем ссылку на вершинный шейдер
		int vertexShader_Handle = GLES20
				.glCreateShader(GLES20.GL_VERTEX_SHADER);
		// присоединяем к вершинному шейдеру его код
		GLES20.glShaderSource(vertexShader_Handle, vertexShaderCode);
		// компилируем вершинный шейдер
		GLES20.glCompileShader(vertexShader_Handle);

		// Получаем результат процесса компиляции
		final int[] compileStatus = new int[1];
		GLES20.glGetShaderiv(vertexShader_Handle, GLES20.GL_COMPILE_STATUS,
				compileStatus, 0);

		// Если компиляция не удалась, удаляем шейдер.
		if (compileStatus[0] == 0) {
			GLES20.glDeleteShader(vertexShader_Handle);
			throw new RuntimeException("Error compiling vertex shader.");
		}

		// получаем ссылку на фрагментный шейдер
		int fragmentShader_Handle = GLES20
				.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
		// присоединяем к фрагментному шейдеру его код
		GLES20.glShaderSource(fragmentShader_Handle, fragmentShaderCode);
		// компилируем фрагментный шейдер
		GLES20.glCompileShader(fragmentShader_Handle);

		// Получаем результат процесса компиляции
		final int[] compileStatus1 = new int[1];
		GLES20.glGetShaderiv(fragmentShader_Handle, GLES20.GL_COMPILE_STATUS,
				compileStatus1, 0);

		// Если компиляция не удалась, удаляем шейдер.
		if (compileStatus1[0] == 0) {
			GLES20.glDeleteShader(fragmentShader_Handle);
			throw new RuntimeException("Error compiling fragment shader.");
		}

		// получаем ссылку на шейдерную программу
		programHandle = GLES20.glCreateProgram();
		// присоединяем к шейдерной программе вершинный шейдер
		GLES20.glAttachShader(programHandle, vertexShader_Handle);
		// присоединяем к шейдерной программе фрагментный шейдер
		GLES20.glAttachShader(programHandle, fragmentShader_Handle);
		// компилируем шейдерную программу
		GLES20.glLinkProgram(programHandle);

		// Получаем ссылку на программу.
		final int[] linkStatus = new int[1];
		GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus,
				0);

		// Если ссылку не удалось получить, удаляем программу.
		if (linkStatus[0] == 0) {
			GLES20.glDeleteProgram(programHandle);
			throw new RuntimeException("Error creating program.");
		}

		getHandles();
	}

	// Takes in ids for files to be read
	public Shader(int vID, int fID, Context context) {
		StringBuffer vs = new StringBuffer();
		StringBuffer fs = new StringBuffer();
		// read the files
		try {
			// Read VS first
			InputStream inputStream = context.getResources().openRawResource(
					vID);
			// setup Bufferedreader
			BufferedReader in = new BufferedReader(new InputStreamReader(
					inputStream));

			String read = in.readLine();
			while (read != null) {
				vs.append(read + "\n");
				read = in.readLine();
			}

			vs.deleteCharAt(vs.length() - 1);

			// Now read FS
			inputStream = context.getResources().openRawResource(fID);
			// setup Bufferedreader
			in = new BufferedReader(new InputStreamReader(inputStream));

			read = in.readLine();
			while (read != null) {
				fs.append(read + "\n");
				read = in.readLine();
			}

			fs.deleteCharAt(fs.length() - 1);
		} catch (Exception e) {
			Log.d("ERROR-readingShader",
					"Could not read shader: " + e.getLocalizedMessage());
		}
		createProgram(vs.toString(), fs.toString());
	}

	/**
	 * Делает шейдерную программу данного класса активной.
	 */
	public void useProgram() {
		GLES20.glUseProgram(programHandle);
	}

	/**
	 * Устанавливает связь между униформой шейдера u_Texture и текстурой
	 * 
	 * @param texture
	 *            текстура
	 */
	public void linkTexture(Texture texture) {
		// устанавливаем текущую активную программу
		GLES20.glUseProgram(programHandle);
		if (texture != null) {
			// получаем ссылку на униформу u_Texture
			int u_texture_Handle = GLES20.glGetUniformLocation(programHandle,
					"u_Texture");
			// выбираем текущий текстурный блок GL_TEXTURE0
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			// в текстурном блоке GL_TEXTURE0
			// делаем активной текстуру с именем texture.getName()
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.getName());
			// выполняем связь между объектом texture и униформой u_Texture
			// в нулевом текстурном блоке
			GLES20.glUniform1i(u_texture_Handle, 0);
		}
	}

	/**
	 * Получает указатели.
	 */
	private void getHandles() {
		MVPMatrixHandle = GLES20.glGetUniformLocation(programHandle,
				"u_MVPMatrix");
		shodowProjMatrixHandle = GLES20.glGetUniformLocation(programHandle,
				"u_shadowProjMatrix");
		positionHandle = GLES20
				.glGetAttribLocation(programHandle, "a_Position");
		normalHandle = GLES20.glGetAttribLocation(programHandle, "a_Normal");
		textureHandle = GLES20.glGetAttribLocation(programHandle, "a_Texture");
		colorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");
		cameraHandle = GLES20.glGetUniformLocation(programHandle, "u_Camera");
		lightPositionHandle = GLES20.glGetUniformLocation(programHandle,
				"u_LightPosition");
	}

	public int getMVPMatrixHandle() {
		return MVPMatrixHandle;
	}

	public int getPositionHandle() {
		return positionHandle;
	}

	public int getNormalHandle() {
		return normalHandle;
	}

	public int getTextureHandle() {
		return textureHandle;
	}

	public int getColorHandle() {
		return colorHandle;
	}

	public int getProgramHandle() {
		return programHandle;
	}

	public int getCameraHandle() {
		return cameraHandle;
	}

	public int getLightPositionHandle() {
		return lightPositionHandle;
	}

	public int getShodowProjMatrixHandle() {
		return shodowProjMatrixHandle;
	}
}
