package org.axel.autohakusovellus;

public class CarData {
    public String type;
    public int amount;

    public CarData(String type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }
}
