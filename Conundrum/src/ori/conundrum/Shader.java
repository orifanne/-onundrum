package ori.conundrum;

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
		
		// Получаем результат процесса компиляции
	    final int[] compileStatus = new int[1];
	    GLES20.glGetShaderiv(vertexShader_Handle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
	 
	    // Если компиляция не удалась, удаляем шейдер.
	    if (compileStatus[0] == 0)
	    {
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
	    GLES20.glGetShaderiv(fragmentShader_Handle, GLES20.GL_COMPILE_STATUS, compileStatus1, 0);
	 
	    // Если компиляция не удалась, удаляем шейдер.
	    if (compileStatus1[0] == 0)
	    {
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
	    GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
	 
	    // Если ссылку не удалось получить, удаляем программу.
	    if (linkStatus[0] == 0)
	    {
	        GLES20.glDeleteProgram(programHandle);
	        throw new RuntimeException("Error creating program.");
	    }
		
		getHandles();
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
	
	public int getTextureHandle() {
		return textureHandle;
	}
	
	public int getProgramHandle() {
		return programHandle;
	}
}
