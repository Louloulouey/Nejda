package com.sdidsa.nejda.abs.components.controls.audio;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import com.sdidsa.nejda.abs.utils.ErrorHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class AudioProcessing {
    public static byte[] decodeAACToPCM(String filePath) throws IOException {
        MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(filePath);
        MediaFormat format = extractor.getTrackFormat(0);
        String mime = format.getString(MediaFormat.KEY_MIME);

        assert mime != null;
        if (!mime.startsWith("audio/")) {
            throw new IllegalArgumentException("Invalid audio file format");
        }

        MediaCodec decoder = MediaCodec.createDecoderByType(mime);
        decoder.configure(format, null, null, 0);
        decoder.start();

        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

        extractor.selectTrack(0);

        boolean isEOS = false;
        ByteArrayOutputStream pcmData = new ByteArrayOutputStream();
        while (!isEOS) {
            int inputBufferIndex = decoder.dequeueInputBuffer(10000);
            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer = decoder.getInputBuffer(inputBufferIndex);
                assert inputBuffer != null;
                inputBuffer.clear();
                int sampleSize = extractor.readSampleData(inputBuffer, 0);
                if (sampleSize < 0) {
                    decoder.queueInputBuffer(inputBufferIndex, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    isEOS = true;
                } else {
                    decoder.queueInputBuffer(inputBufferIndex, 0, sampleSize, extractor.getSampleTime(), 0);
                    extractor.advance();
                }
            }

            int outputBufferIndex = decoder.dequeueOutputBuffer(bufferInfo, 10000);
            if (outputBufferIndex >= 0) {
                ByteBuffer outputBuffer = decoder.getOutputBuffer(outputBufferIndex);
                byte[] chunk = new byte[bufferInfo.size];
                assert outputBuffer != null;
                outputBuffer.get(chunk);
                outputBuffer.clear();
                pcmData.write(chunk);

                decoder.releaseOutputBuffer(outputBufferIndex, false);
            }
        }

        decoder.stop();
        decoder.release();
        extractor.release();
        return pcmData.toByteArray();
    }

    public static float getMaxLoudness(byte[] pcmData) throws IOException {
        final int BUFFER_SIZE = 2048;

        InputStream inputStream = new ByteArrayInputStream(pcmData);
        byte[] buffer = new byte[BUFFER_SIZE];
        int readBytes;
        float maxAmplitude = 0;

        while ((readBytes = inputStream.read(buffer)) != -1) {
            for (int i = 0; i < readBytes; i += 2) {
                short sample = (short) ((buffer[i + 1] << 8) | (buffer[i] & 0xFF));
                float amplitude = Math.abs(sample) / 32768.0f;
                if (amplitude > maxAmplitude) {
                    maxAmplitude = amplitude;
                }
            }
        }

        inputStream.close();

        return 20 * (float) Math.log10(maxAmplitude);
    }

    public static float getMaxLoudness(File f) {
        try {
            byte[] bytes = decodeAACToPCM(f.getAbsolutePath());
            float loudness = getMaxLoudness(bytes);
            return Float.isFinite(loudness) ? loudness : -100;
        } catch (Exception e) {
            ErrorHandler.handle(e, "detecting max volume of audio file");
            return Float.NaN;
        }
    }
}
