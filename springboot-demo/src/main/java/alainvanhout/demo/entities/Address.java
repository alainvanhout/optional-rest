package alainvanhout.demo.entities;

import java.util.Random;

public class Address {
    private int id;
    private String street;
    private String number;
    private String postalCode;
    private String city;


    public Address(String street, String number, String postalCode, String city) {
        this.street = street;
        this.number = number;
        this.postalCode = postalCode;
        this.city = city;
        Random random = new Random();
        this.id = random.nextInt();
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
