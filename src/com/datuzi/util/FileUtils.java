package com.datuzi.util;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;

/**
 * @author 张建波
 * @since 2020年04月22日
 */
public class FileUtils {

    public static final String DEFAULT_CONFIG_PATH = "d://fundConfig.ini";

    public FileInputStream fileInputStream = null;

    public String configPath;

    private static FileUtils instance = null;

    private Properties p = new Properties();

    public static FileUtils getInstance(){
        if (instance == null) {
            return new FileUtils();
        }
        return instance;
    }

    public FileUtils setConfigPath(String configpath){
        this.configPath =configpath;
        return this;
    }

    public FileUtils loadFile() throws Exception{
        if(StringUtils.isBlank(configPath)){
            configPath = DEFAULT_CONFIG_PATH;
        }
        File file = new File(configPath);
        if(!file.exists()){
            file.createNewFile();
        }
        this.fileInputStream = new FileInputStream(file);
        return this;
    }

    public void write(String str) throws Exception{
        File file = new File(configPath);
        if(!file.exists()){
            file.createNewFile();
        }
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write(str);
        writer.close();
    }


    public ArrayList<String> getList() {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            InputStreamReader inputReader = new InputStreamReader(fileInputStream);
            BufferedReader bf = new BufferedReader(inputReader);
            // 按行读取字符串
            String str;
            while ((str = bf.readLine()) != null) {
                arrayList.add(str);
            }
            bf.close();
            inputReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 返回数组
        return arrayList;
    }

    public Map<String,String> getMap(){
        Map<String,String> dataMap = new HashMap<>();
        try {
            p.load(fileInputStream);
            Enumeration em=p.propertyNames() ;
            while(em.hasMoreElements())
            {
                String key=(String)em.nextElement();
                String value=p.getProperty(key);
                dataMap.put(key,value);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return dataMap;
    }

    public void setValue(String key,Object value){
        try {
            p.load(fileInputStream);
            p.setProperty(key,value.toString());
            FileOutputStream fileOutputStream = new FileOutputStream(new File(configPath));
            p.store(fileOutputStream,null);
            fileOutputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getValue(String key,String defaultValue){
        try {
            p.load(fileInputStream);
            p.getProperty(key,defaultValue);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getValue(String key){
        try {
            p.load(fileInputStream);
            p.getProperty(key);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
