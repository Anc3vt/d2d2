package com.ancevt.d2d2.display.shader;

public interface ShaderProgram {

    void attachShader(Shader shader);

    void detachShader(Shader shader);

    int getId();

    void link();

    void use();

    void stop();

    void delete();

    public int getAttribLocation(String name);

    void setAttrib1f(String name, float value);

    void setAttrib2f(String name, float value1, float value2);

    void setAttrib3f(String name, float value1, float value2, float value3);

    void setAttrib4f(String name, float value1, float value2, float value3, float value4);

    int getUniformLocation(String name);

    void setUniform1f(String name, float value);

    void setUniform2f(String name, float value1, float value2);

    void setUniform3f(String name, float value1, float value2, float value3);

    void setUniform4f(String name, float value1, float value2, float value3, float value4);
}
