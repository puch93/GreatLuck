package kr.co.planet.newgreatluck.data;

import java.util.ArrayList;

import lombok.Data;

@Data
public class CharmAdsManagerData {
    ArrayList<CharmData> charmAdsList01;
    ArrayList<CharmData> charmAdsList02;
    ArrayList<CharmData> charmAdsList03;
    ArrayList<CharmData> charmAdsList04;
    ArrayList<CharmData> charmAdsList05;
    ArrayList<CharmData> charmAdsList06;


    public void setData(int type, CharmData c1, CharmData c2, CharmData c3) {
        switch (type) {
            case 1:
                charmAdsList01 = new ArrayList<>();
                charmAdsList01.add(c1);
                charmAdsList01.add(c2);
                charmAdsList01.add(c3);
                break;
            case 2:
                charmAdsList02 = new ArrayList<>();
                charmAdsList02.add(c1);
                charmAdsList02.add(c2);
                charmAdsList02.add(c3);
                break;
            case 3:
                charmAdsList03 = new ArrayList<>();
                charmAdsList03.add(c1);
                charmAdsList03.add(c2);
                charmAdsList03.add(c3);
                break;
            case 4:
                charmAdsList04 = new ArrayList<>();
                charmAdsList04.add(c1);
                charmAdsList04.add(c2);
                charmAdsList04.add(c3);
                break;
            case 5:
                charmAdsList05 = new ArrayList<>();
                charmAdsList05.add(c1);
                charmAdsList05.add(c2);
                charmAdsList05.add(c3);
                break;
            case 6:
                charmAdsList06 = new ArrayList<>();
                charmAdsList06.add(c1);
                charmAdsList06.add(c2);
                charmAdsList06.add(c3);
                break;
        }
    }
}
