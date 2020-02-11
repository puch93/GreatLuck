package kr.co.planet.newgreatluck.data;


import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class CharmData implements Serializable {
    String id;
    String idx;
    String name, contents, price;
    String imageUrl;
    String type; //<= 받기 관리자페이지에서 타입만들기
    String year, month, day;
    String purImageUrl;

    public CharmData() { }

    public CharmData(String id, String name, String contents) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.contents = contents;
    }

    public CharmData(String id, String imageUrl, String name, String contents) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.contents = contents;
    }


    public CharmData(String id, String name, String contents, String price, String imageUrl, String idx, String purImageUrl) {
        this.id = id;
        this.name = name;
        this.contents = contents;
        this.price = price;
        this.imageUrl = imageUrl;
        this.idx = idx;
        this.purImageUrl = purImageUrl;
    }
}
