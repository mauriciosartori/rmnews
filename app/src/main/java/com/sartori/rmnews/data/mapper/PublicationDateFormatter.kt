package com.sartori.rmnews.data.mapper

import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle
import java.util.Locale

/** Formats the API timestamp in the device's time zone and locale. */
internal fun formatPublicationDate(
    value: String?,
    zoneId: ZoneId = ZoneId.systemDefault(),
    locale: Locale = Locale.getDefault()
): String? {
    if (value.isNullOrBlank()) return null
    return try {
        OffsetDateTime.parse(value)
            .atZoneSameInstant(zoneId)
            .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT).withLocale(locale))
    } catch (_: DateTimeParseException) {
        null
    }
}
