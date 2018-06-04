package com.sysgears.seleniumbundle.core.mongodb.entities

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import org.bson.types.ObjectId

/**
 * User entity to be used with MongoDatabase.
 */
class User {

    /**
     * user id in database.
     */
    private ObjectId _id

    /**
     * User email.
     */
    private String emailAddress

    /**
     * Displayed name.
     */
    private String userNameDisplay

    /**
     * User password.
     */
    private String password

    /**
     * User status.
     */
    private Boolean isActive

    /**
     * First name of a user.
     */
    private String firstName

    /**
     * Last name of a user.
     */
    private String lastName

    ObjectId getId() {
        _id
    }

    void setId(ObjectId _id) {
        this._id = _id
    }

    String getEmailAddress() {
        emailAddress
    }

    void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress
    }

    String getFirstName() {
        firstName
    }

    void setFirstName(final String firstName) {
        this.firstName = firstName
    }

    String getLastName() {
        lastName
    }

    void setLastName(final String lastName) {
        this.lastName = lastName
    }

    String getUserNameDisplay() {
        userNameDisplay
    }

    void setUserNameDisplay(final String userNameDisplay) {
        this.userNameDisplay = userNameDisplay
    }

    Boolean getIsActive() {
        isActive
    }

    void setIsActive(final Boolean status) {
        this.isActive = status
    }

    String getPassword() {
        password
    }

    void setPassword(String password) {
        this.password = password
    }

    @Override
    String toString() {
        ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
    }

    @Override
    boolean equals(Object obj) {
        // always sets id of comparing element to be equal to the compared id
        this.id = obj.id
        obj.getClass() == getClass() && EqualsBuilder.reflectionEquals(this, obj)
    }

    @Override
    int hashCode() {
        HashCodeBuilder.reflectionHashCode(this)
    }
}
