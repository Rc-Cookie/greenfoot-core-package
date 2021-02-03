package com.github.rccookie.common.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class SerialTools {
    private SerialTools() { }



    public static final boolean store(final Saveable saveable) {
        if(saveable == null) return false;
        return store(saveable, Saveable.getSaveDir(saveable), saveable.getSaveName());
    }

    public static final boolean store(final Serializable object, final String dirPath, final String fileName) {

        final File dir = new File(dirPath);
        if(!dir.exists()) dir.mkdirs();

        final StringBuilder filePath = new StringBuilder(dirPath);
        if(!filePath.toString().endsWith("/")) filePath.append("/");
        filePath.append(fileName).append(".ser");

        try {
            final FileOutputStream fileOut = new FileOutputStream(filePath.toString());
            final ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(object);
            out.close();
            fileOut.close();
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return false;
    }



    public static final <T extends Saveable> T read(Class<T> objectClass, String fileName) {
        return read(Saveable.getSaveDir(objectClass), fileName);
    }

    public static final <T> T read(String dirPath, String fileName) {

        final StringBuilder filePath = new StringBuilder(dirPath);
        if(!filePath.toString().endsWith("/")) filePath.append("/");
        filePath.append(fileName).append(".ser");

        try{
            final FileInputStream fileIn = new FileInputStream(filePath.toString());
            final ObjectInputStream in = new ObjectInputStream(fileIn);
            @SuppressWarnings("unchecked")
            final T t = (T)in.readObject();
            in.close();
            fileIn.close();
            return t;
        }
        catch(final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
