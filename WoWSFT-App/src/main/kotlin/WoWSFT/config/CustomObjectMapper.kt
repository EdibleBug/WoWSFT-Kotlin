package WoWSFT.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.json.JsonReadFeature
import com.fasterxml.jackson.core.json.JsonWriteFeature
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature

class CustomObjectMapper(jsonFactory: JsonFactory) : ObjectMapper(jsonFactory) {
    init {
        checkNotNull(factory) { "JsonFactory should not be null." }
        jsonFactory.codec = this
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
        configure(JsonWriteFeature.WRITE_NUMBERS_AS_STRINGS.mappedFeature(), false)
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
        configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true)
    }
}