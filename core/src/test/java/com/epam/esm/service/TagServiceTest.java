package com.epam.esm.service;

import com.epam.esm.entity.Tag;
import com.epam.esm.exception.ResourceAlreadyExistsException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.validator.TagValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;

public class TagServiceTest {

    // To be implemented

    /*
    private final TagDao tagDao = Mockito.mock(TagDao.class);
    private final TagValidator tagValidator = Mockito.mock(TagValidator.class);
    private final TagService tagService = new TagService(tagDao, tagValidator);

    @Test
    public void testShouldReturnListOfTagsInGetAll() {
        List<Tag> expected = Arrays.asList(new Tag(), new Tag());
        Mockito.when(tagDao.getAll()).thenReturn(expected);

        List<Tag> actual = tagService.getAll();

        assertEquals(expected, actual);
    }

    @Test
    public void testShouldReturnTagInGetById() {
        long id = 0;
        Tag expected = new Tag();
        Mockito.when(tagDao.getById(id)).thenReturn(Optional.of(expected));

        Tag actual = tagService.getById(id);

        assertEquals(expected, actual);
    }

    @Test
    public void testShouldThrowExceptionWhenResourceIsNotFoundById() {
        assertThrows(ResourceNotFoundException.class, () -> {
            long id = 0;
            Mockito.when(tagDao.getById(id)).thenReturn(Optional.empty());

            tagService.getById(id);
        });
    }

    @Test
    public void testShouldReturnTagInGetByName() {
        Tag expected = new Tag();
        Mockito.when(tagDao.getByName(null)).thenReturn(Optional.of(expected));

        Tag actual = tagService.getByName(null);

        assertEquals(expected, actual);
    }

    @Test
    public void testShouldThrowExceptionWhenResourceIsNotFoundByName() {
        assertThrows(ResourceNotFoundException.class, () -> {
            Mockito.when(tagDao.getByName(null)).thenReturn(Optional.empty());

            tagService.getByName(null);
        });
    }

    @Test
    public void testShouldWorkCorrectlyInCreate() {
        String name = "name";
        Tag tag = new Tag(0L, name);
        Mockito.when(tagDao.getByName(name)).thenReturn(Optional.empty());
        Mockito.when(tagDao.create(tag)).thenReturn(0L);

        tagService.create(tag);
    }

    @Test
    public void testShouldThrowExceptionWhenTagWithSameNameExistsInCreate() {
        assertThrows(ResourceAlreadyExistsException.class, () -> {
            String name = "name";
            Tag tag = new Tag(0L, name);
            Mockito.when(tagDao.getByName(name)).thenReturn(Optional.of(new Tag()));

            tagService.create(tag);
        });
    }

    @Test
    public void testShouldWorkCorrectlyInCreateTagsIfNotPresent() {
        String name = "name";
        Tag tag = new Tag(null, name);
        List<Tag> tags = List.of(tag);
        Mockito.when(tagDao.getByName(name)).thenReturn(Optional.empty());
        Long tagId = 0L;
        List<Long> expectedAutoGeneratedIds = List.of(tagId);
        Mockito.when(tagDao.create(tag)).thenReturn(tagId);

        List<Long> actualAutoGeneratedIds = tagService.createTagsIfNotCreated(tags);

        assertEquals(expectedAutoGeneratedIds, actualAutoGeneratedIds);
    }

    @Test
    public void testShouldWorkCorrectlyInGetTagsByListOfIds() {
        Long tagId = 0L;
        List<Long> tagIds = List.of(tagId);
        Tag tag = new Tag(tagId, "name");
        Mockito.when(tagDao.getById(tagId)).thenReturn(Optional.of(tag));
        List<Tag> expected = List.of(tag);

        List<Tag> actual = tagService.getTagsByListOfIds(tagIds);

        assertEquals(expected, actual);
    }

    @Test
    public void testShouldWorkCorrectlyInDeleteById() {
        long id = 0;
        Mockito.when(tagDao.getById(id)).thenReturn(Optional.of(new Tag()));
        doNothing().when(tagDao).deleteById(id);

        tagService.deleteById(id);
    }

    @Test
    public void testShouldThrowExceptionWhenResourceIsNotFoundBeforeDeleting() {
        assertThrows(ResourceNotFoundException.class, () -> {
            long id = 0;
            Mockito.when(tagDao.getById(id)).thenReturn(Optional.empty());

            tagService.deleteById(id);
        });
    }*/
}
