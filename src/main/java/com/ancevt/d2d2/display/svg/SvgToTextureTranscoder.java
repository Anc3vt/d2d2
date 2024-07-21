package com.ancevt.d2d2.display.svg;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.asset.Assets;
import com.ancevt.d2d2.display.texture.Texture;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

public class SvgToTextureTranscoder {

    public static Texture transcodeSvgToTexture(InputStream svgInputStream, Map<Object, Object> svgTranscodingHints) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PNGTranscoder transcoder = new PNGTranscoder();

        svgTranscodingHints.forEach((key, value) -> transcoder.addTranscodingHint((TranscodingHints.Key) key, value));

        TranscoderInput input = new TranscoderInput(svgInputStream);
        TranscoderOutput output = new TranscoderOutput(byteArrayOutputStream);
        try {
            transcoder.transcode(input, output);
        } catch (TranscoderException e) {
            throw new IllegalArgumentException(e);
        }
        return D2D2.textureManager().loadTexture(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
    }

    public static Texture transcodeSvgToTexture(InputStream svgInputStream, float desiredWidth, float desiredHeight) {
        return transcodeSvgToTexture(svgInputStream, Map.of(
                PNGTranscoder.KEY_WIDTH, (int) desiredWidth,
                PNGTranscoder.KEY_HEIGHT, (int) desiredHeight
        ));
    }

    public static Texture transcodeSvgToTexture(InputStream svgInputStream) {
        return transcodeSvgToTexture(svgInputStream, Map.of());
    }

    public static Texture transcodeSvgToTexture(String assetPath, Map<Object, Object> svgTranscodingHints) {
        return transcodeSvgToTexture(Assets.getAsset(assetPath), svgTranscodingHints);
    }

    public static Texture transcodeSvgToTexture(String assetPath, float desiredWidth, float desiredHeight) {
        return transcodeSvgToTexture(Assets.getAsset(assetPath), desiredWidth, desiredHeight);
    }

    public static Texture transcodeSvgToTexture(String assetPath) {
        return transcodeSvgToTexture(Assets.getAsset(assetPath));
    }
}
