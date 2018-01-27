package net.leidra.tracker.frontend.utils;

import com.vaadin.v7.data.util.converter.Converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Created by afuentes on 21/04/2017.
 */
public class String2LocalDateTimeConverter implements Converter<String, LocalDateTime> {
    @Override
    public LocalDateTime convertToModel(String s, Class<? extends LocalDateTime> aClass, Locale locale) throws ConversionException {
        return LocalDateTime.parse(s);
    }

    @Override
    public String convertToPresentation(LocalDateTime localDateTime, Class<? extends String> aClass, Locale locale) throws ConversionException {
        if(localDateTime == null) {
            return EMPTY;
        }
        return localDateTime.format(DateTimeFormatter.ofPattern("dd/MM/YYYY hh:mm:ss"));
    }

    @Override
    public Class<LocalDateTime> getModelType() {
        return LocalDateTime.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
