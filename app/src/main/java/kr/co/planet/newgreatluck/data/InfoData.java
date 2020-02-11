package kr.co.planet.newgreatluck.data;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class InfoData extends RealmObject implements Serializable {
    @PrimaryKey
    private String idx;
    private String name, gender, blood, marrige;
    private String year, month, day, calendarKind, moonKind, bornTime;
    private String zodiac, constellation;

    private String zodiacText, constellationText;

    public void setData(String name, String gender, String blood, String marrige,
                        String year, String month, String day, String calendarKind, String moonKind, String bornTime,
                        String zodiac, String constellation) {
        this.name = name;
        this.gender = gender;
        this.blood = blood;
        this.marrige = marrige;
        this.year = year;
        this.month = month;
        this.day = day;
        this.calendarKind = calendarKind;
        this.moonKind = moonKind;
        this.bornTime = bornTime;
        this.zodiac = zodiac;
        this.constellation = constellation;
    }

    public void setDataText(String zodiacText, String constellationText) {
        this.zodiacText = zodiacText;
        this.constellationText = constellationText;
    }
}
