package com.epam.esm.config;

import com.epam.esm.hibernate.GiftAndTagDao;
import com.epam.esm.hibernate.GiftCertificateDao;
import com.epam.esm.hibernate.TagDao;
import com.epam.esm.hibernate.UserDao;
import com.epam.esm.hibernate.UserOrderDao;
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
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.persistence.EntityManager;
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
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        return localContainerEntityManagerFactoryBean;
    }

    @Bean
    public EntityManager entityManager(LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean) {
        return localContainerEntityManagerFactoryBean.createNativeEntityManager(null);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public GiftCertificateDao giftCertificateDao(EntityManager entityManager) {
        return new GiftCertificateDao(entityManager);
    }

    @Bean
    public GiftCertificateService giftCertificateService(EntityManager entityManager) {
        return new GiftCertificateService(giftCertificateDao(entityManager),
                tagService(entityManager),
                giftAndTagDao(entityManager),
                giftCertificateValidator());
    }

    @Bean
    public GiftCertificateValidator giftCertificateValidator() {
        return new GiftCertificateValidator();
    }

    @Bean
    public TagDao tagDao(EntityManager entityManager) {
        return new TagDao(entityManager);
    }

    @Bean
    public TagService tagService(EntityManager entityManager) {
        return new TagService(tagDao(entityManager), userOrderDao(entityManager), tagValidator());
    }

    @Bean
    public TagValidator tagValidator() {
        return new TagValidator();
    }

    @Bean
    public UserDao userDao(EntityManager entityManager) {
        return new UserDao(entityManager);
    }

    @Bean
    public UserService userService(EntityManager entityManager) {
        return new UserService(userDao(entityManager));
    }

    @Bean
    public UserOrderDao userOrderDao(EntityManager entityManager) {
        return new UserOrderDao(entityManager);
    }

    @Bean
    public UserOrderService userOrderService(EntityManager entityManager) {
        return new UserOrderService(
                userOrderDao(entityManager),
                userOrderValidator(),
                userService(entityManager),
                giftCertificateService(entityManager));
    }

    @Bean
    public UserOrderValidator userOrderValidator() {
        return new UserOrderValidator();
    }

    @Bean
    public GiftAndTagDao giftAndTagDao(EntityManager entityManager) {
        return new GiftAndTagDao(entityManager);
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
