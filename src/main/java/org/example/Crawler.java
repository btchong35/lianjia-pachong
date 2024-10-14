package org.example;

import org.example.entity.BaseEntity;
import org.example.entity.ConfigReader;
import org.example.entity.HouseEntity;
import org.example.entity.UrlEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler {


    /**
     * @param city          城市缩写，如：上海sh
     * @param communityName 小区名字（中文）
     * @return 需要爬取的url列表
     */
    public List<UrlEntity> getUrls(String city, String communityName) throws Exception {
        List<UrlEntity> urlList = new ArrayList<>();
        String newCommunityName = "";
        //将中文小区名字转换成百分号形式
        try {
            newCommunityName = URLEncoder.encode(communityName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String firstPageUrl = "https://" + city + ".lianjia.com/chengjiao/rs" + newCommunityName + "/";
        Document document = null;
        ConfigReader configReader=new ConfigReader();
        String requestHeadCookie=configReader.getProperty("requestHeadCookie");
        try {
            document = Jsoup.connect(firstPageUrl).header("cookie", requestHeadCookie).get();
            System.out.println(document.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //根据.m-noresult类来判断是否有查询结果
        Elements elements = document.select(".page-box.house-lst-page-box");
        //如果能查到m-noresult，表示无查询结果
        if (elements.size() == 0) {
            throw new RuntimeException("无查询结果！");
        } else {
            Element element = elements.get(0);
            String pageData = element.attr("page-data");
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(pageData);
            //获取查询结果页数
            if(matcher.find()){
                String pageCountString = matcher.group();
                System.out.println("pageCountString="+pageCountString);
                int pageCountInt = Integer.parseInt(pageCountString);
                for (int i = 1; i <= pageCountInt; i++) {
                    urlList.add(new UrlEntity("https://" + city + ".lianjia.com/chengjiao/pg" + i + "rs" + newCommunityName + "/"));
                }
                return urlList;
            }else{
                throw new Exception("无法找到总页数");
            }
        }
    }

    public List<? extends BaseEntity> crawlData(List<UrlEntity> urlList) throws InterruptedException, IOException {
        List<HouseEntity> houseEntityList = new ArrayList<>();
        ConfigReader configReader=new ConfigReader();
        String requestHeadCookie=configReader.getProperty("requestHeadCookie");
        //遍历url
        for (UrlEntity e : urlList) {
            Document document = Jsoup.connect(e.getUrlAddress()).header("cookie",requestHeadCookie ).get();
            Elements elements = document.select(".info");
            System.out.println("elements.size="+elements.size());
            for (Element f : elements) {
                HouseEntity houseEntity = new HouseEntity();
                //获取房源信息标题
                Element titleElement = f.select(".title > a").get(0);
                //如果是车位，直接进入下一轮循环
                if (titleElement.text().contains("车位")) continue;
                //提取小区名字
                String communityName = titleElement.text().split(" ")[0];
                houseEntity.setCommunityName(communityName);
                //提取卧室数量
                Pattern bedroomNumberPattern = Pattern.compile("(?<= )\\d+?(?=室)");
                Matcher bedroomNumberMatcher = bedroomNumberPattern.matcher(titleElement.text());
                String bedroomNumber = "";
                if (bedroomNumberMatcher.find()) {
                    bedroomNumber = bedroomNumberMatcher.group();
                } else {
                    bedroomNumber = "0";
                }
                houseEntity.setBedroomCount(bedroomNumber);
                //提取客厅数量
                Pattern livingroomNumberPattern = Pattern.compile("(?<=室)\\d+?(?=厅)");
                Matcher livingroomNumberMatcher = livingroomNumberPattern.matcher(titleElement.text());
                String livingroomNumber = "";
                if (livingroomNumberMatcher.find()) {
                    livingroomNumber = livingroomNumberMatcher.group();
                } else {
                    livingroomNumber = "0";
                }
                houseEntity.setLivingRoomCount(livingroomNumber);
                //提取房子面积
                Pattern houseSpacePattern = Pattern.compile("(?<=厅 ).+?(?=平米)");
                Matcher houseSpaceMatcher = houseSpacePattern.matcher(titleElement.text());
                String houseSpace = "";
                if (houseSpaceMatcher.find()) {
                    houseSpace = houseSpaceMatcher.group();
                } else {
                    houseSpace = "0";
                }
                houseEntity.setHouseSpace(houseSpace);
                //获取上架日期
                Element dealDateElement = f.select(".address .dealDate").get(0);
                houseEntity.setDealDate(dealDateElement.text());
                //获取房源总价
                Element totalPriceElement = f.select(".address .totalPrice span").get(0);
                houseEntity.setTotalPrice(totalPriceElement.text());
                //获取房源位置信息
                Element positionInfoElement = f.select(".flood .positionInfo").get(0);
                //获取房源建造年份
                Pattern buildYearPatter = Pattern.compile("(?<= ).+?(?=年)");
                Matcher buildYearMatcher = buildYearPatter.matcher(positionInfoElement.text());
                String buildYear = "";
                if (buildYearMatcher.find()) {
                    buildYear = buildYearMatcher.group();
                } else {
                    buildYear = "0";
                }
                houseEntity.setBuildYear(buildYear);
                //获取房源单价
                Element unitPriceElement = f.select(".flood .unitPrice span").get(0);
                houseEntity.setUnitePrice(unitPriceElement.text());
                //获取dealCycleTxt对象
                Elements dealCycleeInfoElements = f.select(".dealCycleeInfo .dealCycleTxt");
                //获取挂牌价和成交周期的全部txt
                String dealCycleTxt = dealCycleeInfoElements.get(0).text();
                System.out.println("dealCycleTxt="+dealCycleTxt);
                //使用正则提取挂牌价
                Pattern guapaiPricePattern = Pattern.compile("(?<=挂牌).+?(?=万)");
                Matcher guapaiPriceMatcher = guapaiPricePattern.matcher(dealCycleTxt);
                if (guapaiPriceMatcher.find()) {
                    houseEntity.setListingPrice(guapaiPriceMatcher.group());
                } else {
                    houseEntity.setListingPrice("0");
                }
                //使用正则提取成交周期
                Pattern chengjiaozhouqiPattern = Pattern.compile("(?<=周期)\\d+?(?=天)");
                Matcher chengjiaozhouqiMatcher = chengjiaozhouqiPattern.matcher(dealCycleTxt);
                if (chengjiaozhouqiMatcher.find()) {
                    houseEntity.setSellingPeriod(chengjiaozhouqiMatcher.group());
                } else {
                    houseEntity.setSellingPeriod("0");
                }
                System.out.println("lianJiaHouseEntity.toString()="+ houseEntity.toString());
                houseEntityList.add(houseEntity);
                Thread.sleep(50);
            }
        }
        return houseEntityList;
    }

    public void writeDataToTxt(List<? extends BaseEntity> dataList, String txtPath) {

    }
}
