#version 300 es
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float;

//纹理坐标，图片当中的坐标点
in vec2 texCoord;
//输出颜色，GL内建变量
out vec4 outColor;

//图片，采样器
uniform samplerExternalOES s_texture;

void main(){
    outColor = texture(s_texture, texCoord);
}