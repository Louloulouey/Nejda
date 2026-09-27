package com.sdidsa.nejda.abs.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class Assets {
    public static String readAsset(Context context, String path) {
        try ( BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().open(path)))) {
            StringBuilder sb = new StringBuilder();

            String line;
            while((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString().trim();
        }catch(IOException x) {
            ErrorHandler.handle(x, "reading asset at ".concat(path));
            return null;
        }
    }

    public static String readFile(File file) {
        try ( BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();

            String line;
            while((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString().trim();
        }catch(IOException x) {
            ErrorHandler.handle(x, "reading file at ".concat(file.getAbsolutePath()));
            return null;
        }
    }

    public static void writeFile(File file, String toWrite) {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            bw.write(toWrite);
        }catch(IOException x) {
            ErrorHandler.handle(x, "writing file at ".concat(file.getAbsolutePath()));
        }
    }
}
