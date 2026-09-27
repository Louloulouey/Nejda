package org.luke.controll.abs;


import org.json.JSONObject;
import org.luke.gui.exception.ErrorHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.Consumer;

public class Cloudinary {
    public static void uploadAttachment(File file, String uploadPreset,
                                        Consumer<String> onSuccess) {
        new Thread(() -> {
            String boundary = "===" + System.currentTimeMillis() + "===";
            String LINE_FEED = "\r\n";

            try {
                URL url = new URL("https://api.cloudinary.com/v1_1/" +
                        "dyrjef3ry" + "/auto/upload");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setUseCaches(false);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                OutputStream outputStream = conn.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream,
                        StandardCharsets.UTF_8), true);

                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"upload_preset\"").append(LINE_FEED);
                writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
                writer.append(LINE_FEED).append(uploadPreset).append(LINE_FEED);
                writer.flush();

                String mimeType = Files.probeContentType(file.toPath());
                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                        .append(file.getName()).append("\"").append(LINE_FEED);
                writer.append("Content-Type: ").append(mimeType).append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.flush();

                Files.copy(file.toPath(), outputStream);
                outputStream.flush();
                writer.append(LINE_FEED).flush();

                writer.append("--").append(boundary).append("--").append(LINE_FEED);
                writer.close();

                int status = conn.getResponseCode();
                InputStream is = (status == HttpURLConnection.HTTP_OK) ? conn.getInputStream() : conn.getErrorStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                conn.disconnect();

                if (status == HttpURLConnection.HTTP_OK) {
                    JSONObject json = new JSONObject(response.toString());
                    String secureUrl = json.getString("secure_url");
                    onSuccess.accept(secureUrl);
                } else {
                    throw new RuntimeException("Upload failed: " + response);
                }

            } catch (Exception e) {
                ErrorHandler.handle(e, "upload to cloudinary");
            }
        }).start();
    }

}
