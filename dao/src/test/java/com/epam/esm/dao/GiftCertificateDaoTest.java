package com.epam.esm.dao;

import com.epam.esm.config.TestConfig;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.mapper.GiftCertificateRowMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
public class GiftCertificateDaoTest {

    private static EmbeddedDatabase database;
    private static GiftCertificateDao giftCertificateDao;

    @BeforeAll
    public static void setUp() {
        database = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql")
                .addScript("data.sql")
                .build();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(database);
        GiftCertificateRowMapper giftCertificateRowMapper = new GiftCertificateRowMapper();
        giftCertificateDao = new GiftCertificateDao(jdbcTemplate, giftCertificateRowMapper);
    }

    @AfterAll
    public static void tearDown() {
        database.shutdown();
    }

    @Test
    public void testShouldGetAllGiftCertificates() {
        LocalDateTime firstDate = LocalDateTime.parse("2021-07-02T06:12:15.156", DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime secondDate = LocalDateTime.parse("2018-08-29T06:12:15.156", DateTimeFormatter.ISO_DATE_TIME);
        GiftCertificate firstGiftCertificate = new GiftCertificate(1L, "CER5", "NORMAL DAY CERTIFICATE", 199, 20L, firstDate, firstDate);
        GiftCertificate secondGiftCertificate = new GiftCertificate(2L, "CER1", "GOOD DAY CERTIFICATE", 50, 10L, secondDate, secondDate);
        List<GiftCertificate> expected = Arrays.asList(firstGiftCertificate, secondGiftCertificate);

        List<GiftCertificate> actual = giftCertificateDao.getAll();

        assertEquals(expected, actual);
    }

    @Test
    public void testShouldGetGiftCertificateById() {
        LocalDateTime localDateTime = LocalDateTime.parse("2021-07-02T06:12:15.156", DateTimeFormatter.ISO_DATE_TIME);
        GiftCertificate expected = new GiftCertificate(1L, "CER5", "NORMAL DAY CERTIFICATE", 199, 20L, localDateTime, localDateTime);

        Optional<GiftCertificate> actual = giftCertificateDao.getById(1);

        assertEquals(expected, actual.get());
    }

    @Test
    public void testShouldGetListOfGiftCertificatesByPartOfField() {
        LocalDateTime firstDate = LocalDateTime.parse("2021-07-02T06:12:15.156", DateTimeFormatter.ISO_DATE_TIME);
        GiftCertificate firstGiftCertificate = new GiftCertificate(1L, "CER5", "NORMAL DAY CERTIFICATE", 199, 20L, firstDate, firstDate);
        List<GiftCertificate> expected = List.of(firstGiftCertificate);

        List<GiftCertificate> actual = giftCertificateDao.getByPartOfField("name", "ER5");

        assertEquals(expected, actual);
    }

    @Test
    public void testShouldSortGiftCertificatesByFieldInAscendingOrder() {
        LocalDateTime firstDate = LocalDateTime.parse("2021-07-02T06:12:15.156", DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime secondDate = LocalDateTime.parse("2018-08-29T06:12:15.156", DateTimeFormatter.ISO_DATE_TIME);
        GiftCertificate firstGiftCertificate = new GiftCertificate(1L, "CER5", "NORMAL DAY CERTIFICATE", 199, 20L, firstDate, firstDate);
        GiftCertificate secondGiftCertificate = new GiftCertificate(2L, "CER1", "GOOD DAY CERTIFICATE", 50, 10L, secondDate, secondDate);
        List<GiftCertificate> expected = Arrays.asList(secondGiftCertificate, firstGiftCertificate);

        List<GiftCertificate> actual = giftCertificateDao.sortByFieldInGivenOrder("name", true);

        assertEquals(expected, actual);
    }

    @Test
    public void testShouldSortGiftCertificatesByFieldInDescendingOrder() {
        LocalDateTime firstDate = LocalDateTime.parse("2021-07-02T06:12:15.156", DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime secondDate = LocalDateTime.parse("2018-08-29T06:12:15.156", DateTimeFormatter.ISO_DATE_TIME);
        GiftCertificate firstGiftCertificate = new GiftCertificate(1L, "CER5", "NORMAL DAY CERTIFICATE", 199, 20L, firstDate, firstDate);
        GiftCertificate secondGiftCertificate = new GiftCertificate(2L, "CER1", "GOOD DAY CERTIFICATE", 50, 10L, secondDate, secondDate);
        List<GiftCertificate> expected = Arrays.asList(firstGiftCertificate, secondGiftCertificate);

        List<GiftCertificate> actual = giftCertificateDao.sortByFieldInGivenOrder("create_date", false);

        assertEquals(expected, actual);
    }

    @Test
    public void testShouldCreateGiftCertificate() {
        LocalDateTime date = LocalDateTime.parse("2021-07-02T06:12:15.156", DateTimeFormatter.ISO_DATE_TIME);
        GiftCertificate expected = new GiftCertificate(1L, "Some certificate", "none", 1, 1L, date, date);

        long certificateId = giftCertificateDao.create(expected);

        expected.setId(certificateId);
        Optional<GiftCertificate> actual = giftCertificateDao.getById(certificateId);
        assertEquals(expected, actual.get());
    }

    @Test
    public void testShouldUpdateGiftCertificate() {
        LocalDateTime firstDate = LocalDateTime.parse("2021-07-02T06:12:15.156", DateTimeFormatter.ISO_DATE_TIME);
        GiftCertificate giftCertificate = new GiftCertificate(1L, "CER5", "NORMAL DAY CERTIFICATE", 199, 20L, firstDate, firstDate);
        giftCertificate.setName("Another name");
        Map<String, String> fieldNameValueForUpdate = new HashMap<>();
        fieldNameValueForUpdate.put("NAME", "Another name");

        giftCertificateDao.update(1L, fieldNameValueForUpdate);

        Optional<GiftCertificate> actual = giftCertificateDao.getById(giftCertificate.getId());
        assertEquals(giftCertificate, actual.get());
    }

    @Test
    public void testShouldDeleteGiftCertificateById() {
        LocalDateTime firstDate = LocalDateTime.parse("2021-07-02T06:12:15.156", DateTimeFormatter.ISO_DATE_TIME);
        GiftCertificate giftCertificate = new GiftCertificate(1L, "CER5", "NORMAL DAY CERTIFICATE", 199, 20L, firstDate, firstDate);

        giftCertificateDao.delete(giftCertificate.getId());

        Optional<GiftCertificate> actual = giftCertificateDao.getById(giftCertificate.getId());
        assertTrue(actual.isEmpty());
    }
}
