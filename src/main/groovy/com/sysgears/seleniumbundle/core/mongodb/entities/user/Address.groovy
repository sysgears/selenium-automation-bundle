package com.sysgears.seleniumbundle.core.mongodb.entities.user

/**
 * Address entity to be used with Mongo database.
 */
class Address {

    /**
     * City.
     */
    private String city

    /**
     * Address line.
     */
    private String addressLine

    /**
     * Zip code.
     */
    private String zip

    String getCity() {
        return city
    }

    void setCity(String city) {
        this.city = city
    }

    String getAddressLine() {
        return addressLine
    }

    void setAddressLine(String addressLine) {
        this.addressLine = addressLine
    }

    String getZip() {
        return zip
    }

    void setZip(String zip) {
        this.zip = zip
    }
}
