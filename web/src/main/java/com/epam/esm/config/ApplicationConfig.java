package com.epam.esm.config;

import com.epam.esm.dao.GiftAndTagDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.UserDao;
import com.epam.esm.dao.UserOrderDao;
import com.epam.esm.mapper.GiftCertificateRowMapper;
import com.epam.esm.mapper.TagRowMapper;
import com.epam.esm.mapper.UserOrderRowMapper;
import com.epam.esm.mapper.UserRowMapper;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.TagService;
import com.epam.esm.service.UserOrderService;
import com.epam.esm.service.UserService;
import com.epam.esm.validator.GiftCertificateValidator;
import com.epam.esm.validator.TagValidator;
import com.epam.esm.validator.UserOrderValidator;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;

@Configuration
@ComponentScan("com.epam.esm")
@EnableWebMvc
@EnableTransactionManagement
@PropertySource("classpath:database/prod_database.properties")
public class ApplicationConfig implements WebMvcConfigurer {

    private static final String MESSAGE_SOURCE = "messageSource";
    private static final String MESSAGES_BASENAME = "languages/language";
    private static final String LOCALE = "locale";

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public GiftCertificateRowMapper giftCertificateRowMapper() {
        return new GiftCertificateRowMapper();
    }

    @Bean
    public TagRowMapper tagRowMapper() {
        return new TagRowMapper();
    }

    @Bean
    public UserRowMapper userRowMapper() {
        return new UserRowMapper();
    }

    @Bean
    public UserOrderRowMapper userOrderRowMapper() {
        return new UserOrderRowMapper();
    }

    @Bean
    public GiftCertificateDao giftCertificateDao(JdbcTemplate jdbcTemplate) {
        return new GiftCertificateDao(jdbcTemplate, giftCertificateRowMapper());
    }

    @Bean
    public GiftCertificateService giftCertificateService(JdbcTemplate jdbcTemplate) {
        return new GiftCertificateService(giftCertificateDao(jdbcTemplate),
                tagService(jdbcTemplate),
                giftAndTagDao(jdbcTemplate),
                giftCertificateValidator());
    }

    @Bean
    public GiftCertificateValidator giftCertificateValidator() {
        return new GiftCertificateValidator();
    }

    @Bean
    public TagDao tagDao(JdbcTemplate jdbcTemplate) {
        return new TagDao(jdbcTemplate, tagRowMapper());
    }

    @Bean
    public TagService tagService(JdbcTemplate jdbcTemplate) {
        return new TagService(tagDao(jdbcTemplate), tagValidator());
    }

    @Bean
    public TagValidator tagValidator() {
        return new TagValidator();
    }

    @Bean
    public UserDao userDao(JdbcTemplate jdbcTemplate) {
        return new UserDao(jdbcTemplate, userRowMapper());
    }

    @Bean
    public UserService userService(JdbcTemplate jdbcTemplate) {
        return new UserService(userDao(jdbcTemplate));
    }

    @Bean
    public UserOrderDao userOrderDao(JdbcTemplate jdbcTemplate) {
        return new UserOrderDao(jdbcTemplate, userOrderRowMapper());
    }

    @Bean
    public UserOrderService userOrderService(JdbcTemplate jdbcTemplate) {
        return new UserOrderService(userOrderDao(jdbcTemplate), userOrderValidator(), userService(jdbcTemplate), giftCertificateService(jdbcTemplate));
    }

    @Bean
    public UserOrderValidator userOrderValidator() {
        return new UserOrderValidator();
    }

    @Bean
    public GiftAndTagDao giftAndTagDao(JdbcTemplate jdbcTemplate) {
        return new GiftAndTagDao(jdbcTemplate);
    }

    @Bean(MESSAGE_SOURCE)
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(MESSAGES_BASENAME);
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        return messageSource;
    }

    @Bean
    public LocaleResolver localeResolver() {
        return new CookieLocaleResolver();
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName(LOCALE);
        return localeChangeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
