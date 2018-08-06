package com.sysgears.seleniumbundle.tests.mongo

import com.sysgears.seleniumbundle.common.FunctionalTestWithMongo
import com.sysgears.seleniumbundle.core.mongodb.dao.UserDAO
import com.sysgears.seleniumbundle.core.mongodb.entities.User
import com.sysgears.seleniumbundle.core.mongodb.entities.user.Address
import org.bson.types.ObjectId
import org.testng.Assert
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

/**
 * Demonstrates simple use cases of Mongo module.
 */
class MongoExample extends FunctionalTestWithMongo {

    @BeforeMethod
    void restoreDatabase() {
        mongoService.importMongoCollectionsFromJson()
    }


    @Test
    void getUserFromMongoDb() {
        User userFromDb = new UserDAO(dbConnection).findUserBy("test_user@gmail.com")

        def address = new Address(city: "Dnipro", addressLine: "Karl Marx avenue, 25", zip: "49000")
        Assert.assertEquals(address.properties, userFromDb.address.properties)

        def user = new User(_id: new ObjectId("5b080ca6320b963e2efaa5be"), emailAddress: "test_user@gmail.com",
                userNameDisplay: "Test User", password: "password", isActive: true, firstName: "Test", lastName: "User",
                address: userFromDb.address)

        Assert.assertEquals(user, userFromDb)
    }
}
