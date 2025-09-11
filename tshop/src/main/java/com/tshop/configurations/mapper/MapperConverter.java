package com.tshop.configurations.mapper;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Component
public class MapperConverter {

    @Named("timestampToEpochSecond")
    public Long timestampToEpochSecond(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toInstant().getEpochSecond();
    }

    @Named("longToTimestamp")
    public Timestamp longToTimestamp(Long epochSeconds) {
        if (epochSeconds == null) {
            return null;
        }
        return Timestamp.from(Instant.ofEpochSecond(epochSeconds));
    }

    @Named("stringToUUID")
    static UUID stringToUUID(String value) {
        return value == null ? null : UUID.fromString(value);
    }
}
