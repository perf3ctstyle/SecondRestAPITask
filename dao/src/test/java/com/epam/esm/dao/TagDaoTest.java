package com.epam.esm.dao;

import com.epam.esm.config.TestConfig;
import com.epam.esm.entity.Tag;
import com.epam.esm.mapper.TagRowMapper;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
public class TagDaoTest {

    private static EmbeddedDatabase database;
    private static TagDao tagDao;

    @BeforeAll
    public static void setUp() {
        database = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql")
                .addScript("data.sql")
                .build();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(database);
        TagRowMapper tagRowMapper = new TagRowMapper();
        tagDao = new TagDao(jdbcTemplate, tagRowMapper);
    }

    @AfterAll
    public static void tearDown() {
        database.shutdown();
    }

    @Test
    public void testShouldGetAllTags() {
        Tag firstTag = new Tag(1L, "Present");
        Tag secondTag = new Tag(2L, "Past");
        Tag thirdTag = new Tag(3L, "Perfect");
        List<Tag> expected = Arrays.asList(firstTag, secondTag, thirdTag);

        List<Tag> actual = tagDao.getAll();

        assertEquals(expected, actual);
    }

    @Test
    public void testShouldCreateTag() {
        Tag expected = new Tag("Continuous");
        long tagId = tagDao.create(expected);
        expected.setId(tagId);

        Optional<Tag> actual = tagDao.getById(tagId);

        assertEquals(expected, actual.get());
    }

    @Test
    public void testShouldGetTagById() {
        Tag expected = new Tag(1L, "Present");

        Optional<Tag> actual = tagDao.getById(expected.getId());

        assertEquals(expected, actual.get());
    }

    @Test
    public void testShouldGetTagByName() {
        Tag expected = new Tag(2L, "Past");

        Optional<Tag> actual = tagDao.getByName("Past");

        assertEquals(expected, actual.get());
    }

    @Test
    public void testShouldDeleteTagById() {
        long tagId = 3;

        tagDao.delete(tagId);

        Optional<Tag> optionalTag = tagDao.getById(tagId);
        assertTrue(optionalTag.isEmpty());
    }
}
