package com.epam.esm.service;

import com.epam.esm.constructor.GiftCertificateObjectConstructor;
import com.epam.esm.dto.SearchInfoDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.hibernate.GiftAndTagDao;
import com.epam.esm.hibernate.GiftCertificateDao;
import com.epam.esm.validator.GiftCertificateValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

public class GiftCertificateServiceTest {

    private final GiftCertificateDao giftCertificateDao = Mockito.mock(GiftCertificateDao.class);
    private final TagService tagService = Mockito.mock(TagService.class);
    private final GiftAndTagDao giftAndTagDao = Mockito.mock(GiftAndTagDao.class);
    private final GiftCertificateValidator giftCertificateValidator = Mockito.mock(GiftCertificateValidator.class);
    private final GiftCertificateObjectConstructor giftCertificateObjectConstructor = Mockito.mock(GiftCertificateObjectConstructor.class);
    private final GiftCertificateService giftCertificateService = new GiftCertificateService(
            giftCertificateDao,
            tagService,
            giftAndTagDao,
            giftCertificateValidator,
            giftCertificateObjectConstructor
    );

    private final List<GiftCertificate> giftCertificates = new ArrayList<>();
    private final List<Tag> tags = new ArrayList<>();
    private final List<Long> tagIds = new ArrayList<>();
    private final GiftCertificate giftCertificate = new GiftCertificate();
    private final long giftCertificateId = 1L;
    private final Optional<GiftCertificate> optionalGiftCertificate = Optional.of(giftCertificate);

    @Test
    public void testShouldGetAllGiftCertificatesWhenPaginationParametersAreAccurate() {
        int limit = 3;
        int offset = 2;
        giftCertificate.setId(giftCertificateId);
        Mockito.when(giftCertificateDao.getAll(limit, offset)).thenReturn(giftCertificates);
        Mockito.when(giftAndTagDao.getTagIdsByCertificateId(giftCertificateId)).thenReturn(tagIds);
        Mockito.when(tagService.getTagsByListOfIds(tagIds)).thenReturn(tags);

        List<GiftCertificate> actual = giftCertificateService.getAll(limit, offset);

        Assertions.assertEquals(giftCertificates, actual);
    }

    @Test
    public void testShouldGetGiftCertificatesByFilters() {
        int limit = 1;
        int offset = 1;
        SearchInfoDto searchInfoDto = new SearchInfoDto();
        String[] tagNames = new String[0];
        giftCertificate.setId(giftCertificateId);
        Mockito.when(giftCertificateDao.getGiftCertificates(searchInfoDto, tagNames, limit, offset)).thenReturn(giftCertificates);
        Mockito.when(giftAndTagDao.getTagIdsByCertificateId(giftCertificateId)).thenReturn(tagIds);
        Mockito.when(tagService.getTagsByListOfIds(tagIds)).thenReturn(tags);

        List<GiftCertificate> actual = giftCertificateService.getGiftCertificates(searchInfoDto, tagNames, limit, offset);

        Assertions.assertEquals(giftCertificates, actual);
    }

    @Test
    public void testShouldGetGiftCertificateById() {
        giftCertificate.setId(giftCertificateId);
        Mockito.when(giftCertificateDao.getById(giftCertificateId)).thenReturn(optionalGiftCertificate);
        Mockito.when(giftAndTagDao.getTagIdsByCertificateId(giftCertificateId)).thenReturn(tagIds);
        Mockito.when(tagService.getTagsByListOfIds(tagIds)).thenReturn(tags);

        GiftCertificate actual = giftCertificateService.getById(giftCertificateId);

        Assertions.assertEquals(giftCertificate, actual);
    }

    @Test
    public void testShouldThrowExceptionWhenGiftCertificateWasNotFoundById() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            Mockito.when(giftCertificateDao.getById(giftCertificateId)).thenReturn(Optional.empty());

            giftCertificateService.getById(giftCertificateId);
        });
    }

    @Test
    public void testShouldReturnGiftCertificateIdWhenCreateGiftCertificate() {
        Mockito.doNothing().when(giftCertificateValidator).validateForCreation(giftCertificate);
        Mockito.when(giftCertificateDao.create(giftCertificate)).thenReturn(giftCertificateId);

        Mockito.when(giftAndTagDao.getTagIdsByCertificateId(giftCertificateId)).thenReturn(tagIds);
        Mockito.when(tagService.createTagsIfNotCreated(tags)).thenReturn(new ArrayList<>());
        Mockito.doNothing().when(giftAndTagDao).create(anyLong(), anyLong());
        Mockito.doNothing().when(giftAndTagDao).delete(anyLong(), anyLong());

        long actualGiftCertificateId = giftCertificateService.create(giftCertificate);

        Assertions.assertEquals(giftCertificateId, actualGiftCertificateId);
    }

    @Test
    public void testShouldWorkCorrectlyInUpdateById() {
        Mockito.when(giftCertificateDao.getById(giftCertificateId)).thenReturn(Optional.of(giftCertificate));
        Mockito.doNothing().when(giftCertificateValidator).validateForUpdate(giftCertificate);
        Mockito.doNothing().when(giftCertificateDao).update(anyLong(), any());

        Mockito.when(giftAndTagDao.getTagIdsByCertificateId(giftCertificateId)).thenReturn(tagIds);
        Mockito.when(tagService.createTagsIfNotCreated(tags)).thenReturn(new ArrayList<>());
        Mockito.doNothing().when(giftAndTagDao).create(anyLong(), anyLong());
        Mockito.doNothing().when(giftAndTagDao).delete(anyLong(), anyLong());

        giftCertificateService.updateById(giftCertificateId, giftCertificate);
    }

    @Test
    public void testShouldThrowExceptionWhenResourceForUpdateIsNotFoundById() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            Mockito.when(giftCertificateDao.getById(giftCertificateId)).thenReturn(Optional.empty());

            giftCertificateService.getById(giftCertificateId);
        });
    }

    @Test
    public void testShouldWorkCorrectlyInDeleteById() {
        Mockito.when(giftCertificateDao.getById(giftCertificateId)).thenReturn(optionalGiftCertificate);
        Mockito.doNothing().when(giftCertificateDao).delete(giftCertificateId);

        giftCertificateService.delete(giftCertificateId);
    }

    @Test
    public void testShouldThrowExceptionWhenResourceIsNotFoundBeforeDeleting() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            Mockito.when(giftCertificateDao.getById(giftCertificateId)).thenReturn(Optional.empty());

            giftCertificateService.delete(giftCertificateId);
        });
    }
}
