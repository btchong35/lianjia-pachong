package org.example.entity;

import lombok.Data;

@Data
public class HouseEntity extends BaseEntity {

    private String communityName;

    private String bedroomCount;

    private String livingRoomCount;

    private String houseSpace;

    private String dealDate;

    private String totalPrice;

    private String buildYear;

    private String unitePrice;

    //挂牌价
    private String listingPrice;

    //成交周期
    private String sellingPeriod;

    @Override
    public String toString() {
        return communityName + "," + bedroomCount + "," + livingRoomCount + "," + houseSpace + "," + dealDate + "," + totalPrice + "," + buildYear + "," + unitePrice + "," + listingPrice + "," + sellingPeriod;
    }
}
