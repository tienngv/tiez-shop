package com.tshop.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Arrays;
import java.util.Locale;


@Configuration
public class CommonConfigs {
    //i18n
    @Bean
    public LocaleResolver localeResolver() {
        var resolver = new AcceptHeaderLocaleResolver();
        var locales = Arrays.asList(Locale.ENGLISH, new Locale("vi"));
        resolver.setSupportedLocales(locales);
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }


}
