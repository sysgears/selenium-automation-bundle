package com.sysgears.seleniumbundle.core.mongodb

import com.mongodb.MongoClient
import org.bson.codecs.Codec
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider

/**
 * A helper class for creating and combining codec registries.
 */
class CustomCodecRegistry implements CodecRegistry {

    /**
     * A registry of Codec instances searchable by the class that the Codec can encode and decode.
     */
    private final CodecRegistry codecRegistry

    /**
     * Creates an instance of {@code CustomCodecRegistry} which contains a registry built from POJOs
     * and default registry that is provided by mongo-java-driver library.
     */
    CustomCodecRegistry() {
        this.codecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()))
    }

    @Override
    <T> Codec<T> get(final Class<T> clazz) {
        return codecRegistry.get(clazz)
    }
}
