package org.example;

import org.example.entity.HouseEntity;
import org.example.entity.UrlEntity;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Crawler crawler=new Crawler();
        StringUtil stringUtil=new StringUtil();
        List<UrlEntity> urlEntityList=crawler.getUrls("sh","明天华城");
        try {
            List<HouseEntity> houseEntityList = (List<HouseEntity>) crawler.crawlData(urlEntityList);
            System.out.println("房源总量="+ houseEntityList.size());
            StringUtil.writeToText(houseEntityList,"D:\\明天华城.txt");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


//        String str="<div><span><span>aaaa</span></span><span>bbbbb</span></div>";
//        Document document=Jsoup.parse(str);
//        Elements elements=document.select("div");
//        Element element=elements.get(0);
//        System.out.println(element.text());
    }
}