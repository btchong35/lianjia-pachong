package org.example;

import org.example.entity.HouseEntity;
import org.example.entity.UrlEntity;
import java.io.IOException;
import java.util.List;

public class Start {
    public static void main(String[] args) {
        Crawler crawler=new Crawler();
        //输入小区名
        String communityName="绿庭尚城";
        //输入导出目录
        txtExporter txtExporter =new txtExporter("D:\\"+communityName+".txt");
        List<UrlEntity> urlEntityList= null;
        try {
            urlEntityList = crawler.getUrls("sh",communityName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            List<HouseEntity> houseEntityList = (List<HouseEntity>) crawler.crawlData(urlEntityList);
            System.out.println("房源总量="+ houseEntityList.size());
            txtExporter.exportToTxt("小区名称,室,厅,面积,成交日期,成交金额,建成年份,成交单价,挂牌金额,成交周期(天)");
            txtExporter.exportToTxt(houseEntityList);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            txtExporter.closeWriter();
        }


//        String str="<div><span><span>aaaa</span></span><span>bbbbb</span></div>";
//        Document document=Jsoup.parse(str);
//        Elements elements=document.select("div");
//        Element element=elements.get(0);
//        System.out.println(element.text());
    }
}