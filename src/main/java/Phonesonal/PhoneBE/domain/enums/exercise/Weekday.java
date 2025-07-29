package Phonesonal.PhoneBE.domain.enums.exercise;

public enum Weekday{
    MONDAY("월"),
    TUESDAY("화"),
    WEDNESDAY("수"),
    THURSDAY("목"),
    FRIDAY("금"),
    SATURDAY("토"),
    SUNDAY("일");

    private final String value;

    Weekday(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
