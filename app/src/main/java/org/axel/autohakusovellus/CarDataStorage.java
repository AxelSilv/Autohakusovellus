package org.axel.autohakusovellus;

import java.util.ArrayList;

public class CarDataStorage {
    public String city;
    public int year;
    private ArrayList<CarData> cars = new ArrayList<>();
    private static CarDataStorage instance;

    private CarDataStorage() {}

    static public CarDataStorage getInstance() {
        if (instance == null) {
            instance = new CarDataStorage();
        }
        return instance;
    }

    public ArrayList<CarData> getCarData(){
        return cars;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void clearData() {
        cars.clear();
    }

    public String getCity() {
        return city;
    }

    public int getYear() {
        return year;
    }

    public void addCarData(CarData car) {
        cars.add(car);
    }
}
