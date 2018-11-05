package com.sysgears.seleniumbundle.core.mongodb.entities.user

/**
 * The address entity to be used with the Mongo database.
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
