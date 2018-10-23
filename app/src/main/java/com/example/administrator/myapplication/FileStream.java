package com.example.administrator.myapplication;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileStream {
    FileOutputStream mFos = null;

    public void openFile(String fileName)
    {
        try {
            mFos = new FileOutputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void closeFile() {
        try {
            mFos.flush();
            mFos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeData(short[] data, int len) {
        byte[] byteData = toByteArray(data, len);
        try {
            mFos.write(byteData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeData(byte[] data, int len) {
        try {
            mFos.write(data, 0, len);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] toByteArray(short[] src, int count) {
        byte[] dest = new byte[count << 1];
        for (int i = 0; i < count; i++) {
            dest[i * 2] = (byte) (src[i] >> 8);
            dest[i * 2 + 1] = (byte) (src[i] >> 0);
        }

        return dest;
    }
}