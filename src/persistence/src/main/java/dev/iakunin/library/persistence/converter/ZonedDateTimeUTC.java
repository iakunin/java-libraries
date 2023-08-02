package dev.iakunin.library.persistence.converter;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/*
 * Has been taken from here: https://gist.github.com/elviejokike/47e18e0836a86fb789e7c00b69e22847
 */
@Converter
public final class ZonedDateTimeUTC implements AttributeConverter<ZonedDateTime, Timestamp> {

    private static final ZoneId UTC_ZONE_ID = ZoneOffset.UTC;
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();

    @Override
    public Timestamp convertToDatabaseColumn(ZonedDateTime zonedDateTime) {
        // Store always in UTC
        return zonedDateTime != null
            ? Timestamp.valueOf(toUtcZoneId(zonedDateTime).toLocalDateTime())
            : null;
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(Timestamp sqlTimestamp) {
        // Read from database (stored in UTC) and return with the system default.
        return sqlTimestamp != null
            ? toDefaultZoneId(sqlTimestamp.toLocalDateTime().atZone(UTC_ZONE_ID))
            : null;
    }

    private ZonedDateTime toUtcZoneId(ZonedDateTime zonedDateTime) {
        return zonedDateTime.withZoneSameInstant(UTC_ZONE_ID);
    }

    private ZonedDateTime toDefaultZoneId(ZonedDateTime zonedDateTime) {
        return zonedDateTime.withZoneSameInstant(DEFAULT_ZONE_ID);
    }

}
