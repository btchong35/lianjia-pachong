package org.example;

import org.example.entity.BaseEntity;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class StringUtil {

    private String textPath;

    private FileWriter fileWriter;

    private BufferedWriter bufferedWriter;

    public StringUtil() {}

    public StringUtil(String textPath) {
        this.textPath = textPath;
        try {
            this.fileWriter=new FileWriter(textPath,false);
            this.bufferedWriter = new BufferedWriter(fileWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

   /* public static String getNumber(String str){
        String regEx="[^0-9]";
        Pattern p= Pattern.compile(regEx);
        Matcher matcher=p.matcher(str);
        String result=matcher.replaceAll("").trim();
        return result;
    }*/
    public  void writeToText(String string,String txtPath) throws IOException {
        bufferedWriter.write(string);
        bufferedWriter.newLine();
        bufferedWriter.close();
        fileWriter.close();
    }

    public  void writeToText(List<? extends BaseEntity> list, String txtPath){
        try {
            //false表示不在原文本后添加
            FileWriter fileWriter = new FileWriter(txtPath, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for(int i=0;i<list.size();i++){
                bufferedWriter.write(list.get(i).toString());
                //如果不是最后一行，在每行数据后添加一个换行
                if(i!=list.size()-1){
                    bufferedWriter.newLine();
                }
            }
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
