package ori.conundrum;

import java.nio.FloatBuffer;

import android.opengl.GLES20;

public class Shader {

	/** Ссылка на шейдерную программу */
	private int programHandle;

	/** Ссылка на униформу матрицы модели-вида-проекции */
	private int MVPMatrixHandle;

	/** Ссылка на атрибут координат */
	private int positionHandle;

	/** Ссылка на атрибут нормали */
	private int normalHandle;

	/** Ссылка на атрибут координат текстуры */
	private int textureHandle;

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
		// получаем ссылку на фрагментный шейдер
		int fragmentShader_Handle = GLES20
				.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
		// присоединяем к фрагментному шейдеру его код
		GLES20.glShaderSource(fragmentShader_Handle, fragmentShaderCode);
		// компилируем фрагментный шейдер
		GLES20.glCompileShader(fragmentShader_Handle);
		// получаем ссылку на шейдерную программу
		programHandle = GLES20.glCreateProgram();
		// присоединяем к шейдерной программе вершинный шейдер
		GLES20.glAttachShader(programHandle, vertexShader_Handle);
		// присоединяем к шейдерной программе фрагментный шейдер
		GLES20.glAttachShader(programHandle, fragmentShader_Handle);
		// компилируем шейдерную программу
		GLES20.glLinkProgram(programHandle);

		getHandles();
	}

	/**
	 * Делает шейдерную программу данного класса активной.
	 */
	public void useProgram() {
		GLES20.glUseProgram(programHandle);
	}

	/**
	 * Устанавливает связь между униформой шейдера u_texture и текстурой
	 * 
	 * @param texture
	 *            текстура
	 */
	public void linkTexture(Texture texture) {
		// устанавливаем текущую активную программу
		GLES20.glUseProgram(programHandle);
		if (texture != null) {
			// получаем ссылку на униформу u_texture
			int u_texture_Handle = GLES20.glGetUniformLocation(programHandle,
					"u_texture");
			// выбираем текущий текстурный блок GL_TEXTURE0
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			// в текстурном блоке GL_TEXTURE0
			// делаем активной текстуру с именем texture.getName()
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.getName());
			// выполняем связь между объектом texture и униформой u_texture
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
		positionHandle = GLES20
				.glGetAttribLocation(programHandle, "a_Position");
		normalHandle = GLES20.glGetAttribLocation(programHandle, "a_Normal");
		textureHandle = GLES20.glGetAttribLocation(programHandle, "a_Texture");
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
}
