package com.sysgears.seleniumbundle.core.mongodb

import com.mongodb.MongoClient
import com.sysgears.seleniumbundle.core.utils.FileHelper
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
     * Creates an instance of {@code CustomCodecRegistry} which contains registry build from pojos
     * and default registry as well.
     */
    CustomCodecRegistry(String pojosPath) {
        def providers = ([pojosPath] + FileHelper.getSubDirs(pojosPath)*.path)
                .collect { getPackageFromPath(it) }
                .collect { PojoCodecProvider.builder().register(it).build() }

        def defaultCodecRegistry = MongoClient.getDefaultCodecRegistry()
        def pojoCodecRegistry = CodecRegistries.fromProviders(providers)

        this.codecRegistry = CodecRegistries.fromRegistries(
                defaultCodecRegistry,
                pojoCodecRegistry)
    }

    @Override
    <T> Codec<T> get(final Class<T> clazz) {
        return codecRegistry.get(clazz)
    }

    /**
     * Converts path relative to src directory to package format like "com.sysgears.seleniumbundle.core"
     *
     * @param path path that should be converted
     *
     * @return package path
     */
    private String getPackageFromPath(String path) {
        def prefix = "src${File.separator}${ path.contains("main") ? "main" : "test" }" +
                "${File.separator}groovy${File.separator}"
        (path - prefix).split(File.separator).join(".")
    }
}
