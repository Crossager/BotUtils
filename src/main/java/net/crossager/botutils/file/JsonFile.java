package net.crossager.botutils.file;

import com.google.gson.Gson;

import java.io.*;

public class JsonFile<T extends Object> {
    private final Class<? extends T> type;
    private final File file;
    private T data;
    private final Gson gson;

    public JsonFile(Class<T> clazz, File file){
        if(file.isFile() || !file.exists()) {
            this.file = file;
            this.type = clazz;
            this.gson = new Gson();
            reload();
        } else {
            throw new IllegalArgumentException("File cannot be a directory");
        }
    }
    public JsonFile(T initialValue, File file){
        if(file.isFile() || !file.exists()) {
            this.file = file;
            this.data = initialValue;
            this.type = (Class<? extends T>) initialValue.getClass();
            this.gson = new Gson();
            reload();
        } else {
            throw new IllegalArgumentException("File cannot be a directory");
        }
    }

    public T set(T data){
        T temp = this.data;
        this.data = data;
        return temp;
    }

    public T getData(){
        return data;
    }

    public boolean save() {
        try {
            if (!file.exists()) file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            out.write(gson.toJson(data).getBytes());
            out.flush();
            out.close();
            return true;
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    public void reload() {
        if (!file.exists()) return;
        try {
            FileInputStream in = new FileInputStream(file);
            data = gson.fromJson(new String(in.readAllBytes()), type);
            in.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
