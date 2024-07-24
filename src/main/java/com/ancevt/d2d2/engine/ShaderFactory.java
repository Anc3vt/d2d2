package com.ancevt.d2d2.engine;

import com.ancevt.d2d2.display.shader.Shader;
import com.ancevt.d2d2.display.shader.ShaderProgram;

public interface ShaderFactory {

    Shader createFragmentShader(String source);

    Shader createVertexShader(String source);

    ShaderProgram createShaderProgram();
}
