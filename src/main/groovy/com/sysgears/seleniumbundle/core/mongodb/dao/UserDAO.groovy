package com.sysgears.seleniumbundle.core.mongodb.dao

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.sysgears.seleniumbundle.core.mongodb.DBConnection
import com.sysgears.seleniumbundle.core.mongodb.entities.User

/**
 * DAO for User entity.
 */
class UserDAO {

    /**
     * Mongo collection associated with User entity.
     */
    private final MongoCollection<User> collection

    /**
     * Constructor for UserDAO.
     *
     * @param connection instance of connection to the database
     */
    UserDAO(DBConnection connection) {
        collection = connection.database.getCollection("users", User.class)
    }

    /**
     * Finds user by email.
     *
     * @param email email of a user
     * @return object of User
     */
    User findUserBy(String email) {
        collection.find(Filters.eq("emailAddress", email)).first()
    }
}
