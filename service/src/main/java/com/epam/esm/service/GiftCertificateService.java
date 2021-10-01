package com.epam.esm.service;

import com.epam.esm.constant.GenericExceptionMessageConstants;
import com.epam.esm.constant.GiftCertificateConstants;
import com.epam.esm.dao.GiftAndTagDao;
import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dto.SearchInfoDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.DaoException;
import com.epam.esm.exception.RequiredFieldMissingException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.util.DateTimeUtils;
import com.epam.esm.validator.GiftCertificateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This is a class that encapsulates the {@link GiftCertificate} business logic and also acts as a transaction boundary.
 *
 * @author Nikita Torop
 */
@Service
public class GiftCertificateService implements com.epam.esm.service.Service<GiftCertificate> {

    private final GiftCertificateDao giftCertificateDao;
    private final TagService tagService;
    private final GiftAndTagDao giftAndTagDao;
    private final GiftCertificateValidator giftCertificateValidator;

    private static final String GIFT_CERTIFICATE = "Gift Certificate";
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    @Autowired
    public GiftCertificateService(GiftCertificateDao giftCertificateDao,
                                  TagService tagService,
                                  GiftAndTagDao giftAndTagDao,
                                  GiftCertificateValidator giftCertificateValidator) {
        this.giftCertificateDao = giftCertificateDao;
        this.tagService = tagService;
        this.giftAndTagDao = giftAndTagDao;
        this.giftCertificateValidator = giftCertificateValidator;
    }

    public List<GiftCertificate> getAll(int limit, int offset) {
        if (limit < 0 || offset < 0) {
            throw new IllegalArgumentException(GenericExceptionMessageConstants.NEGATIVE_VALUE_PROHIBITED);
        }

        List<GiftCertificate> giftCertificates = giftCertificateDao.getAll(limit, offset);
        setGiftCertificateTags(giftCertificates);

        return giftCertificates;
    }

    /**
     * Returns a {@link List} of {@link GiftCertificate} objects from a database.
     *
     * @param searchInfoDto - info for search.
     * @param tagNames - the names of {@link Tag} objects which are linked to the {@link GiftCertificate} objects.
     * @return {@link List} of {@link GiftCertificate} objects.
     */
    public List<GiftCertificate> getGiftCertificates(SearchInfoDto searchInfoDto, String[] tagNames, int limit, int offset) {
        if (limit < 0 || offset < 0) {
            throw new IllegalArgumentException(GenericExceptionMessageConstants.NEGATIVE_VALUE_PROHIBITED);
        }

        SearchInfoDto searchInfoToUse = (searchInfoDto != null) ? searchInfoDto : new SearchInfoDto();
        List<GiftCertificate> giftCertificates = giftCertificateDao.getGiftCertificates(searchInfoToUse);

        if (tagNames != null) {
            for (String tagName : tagNames) {
                Tag tag = tagService.getByName(tagName);
                giftCertificates = returnGiftCertificatesLinkedToTag(tag, giftCertificates);
            }
        }
        if (offset > 0) {
            giftCertificates = returnWithOffset(giftCertificates, offset);
        }
        if (limit > 0) {
            giftCertificates = returnWithLimit(giftCertificates, limit);
        }

        setGiftCertificateTags(giftCertificates);

        return giftCertificates;
    }

    /**
     * Returns a {@link GiftCertificate} object from a database by its id or throws {@link ResourceNotFoundException} if nothing is retrieved from a database
     * or {@link DaoException} in the case of unexpected behaviour on a Dao level.
     *
     * @param id - the {@link GiftCertificate} object's id that is to be retrieved from a database.
     * @return {@link GiftCertificate} object.
     */
    public GiftCertificate getById(long id) {
        Optional<GiftCertificate> optionalGiftCertificate = giftCertificateDao.getById(id);
        GiftCertificate giftCertificate = optionalGiftCertificate.orElseThrow(
                () -> new ResourceNotFoundException(GenericExceptionMessageConstants.RESOURCE_NOT_FOUND, GIFT_CERTIFICATE));

        setGiftCertificateTags(giftCertificate);

        return giftCertificate;
    }

    /**
     * Creates a {@link GiftCertificate} object in a database or throws {@link RequiredFieldMissingException} if some fields
     * required for creation are missing or {@link IllegalArgumentException} if the parameter object's price or duration values are lower than 0.
     *
     * @param giftCertificate - the {@link GiftCertificate} object that is to be created in a database.
     */
    @Transactional
    public long create(GiftCertificate giftCertificate) {
        giftCertificateValidator.validateForCreation(giftCertificate);

        LocalDateTime currentDateTime = DateTimeUtils.nowOfPattern(DATE_TIME_PATTERN);
        giftCertificate.setCreateDate(currentDateTime);
        giftCertificate.setLastUpdateDate(currentDateTime);

        long giftCertificateId = giftCertificateDao.create(giftCertificate);

        List<Tag> tags = giftCertificate.getTags();

        if (tags != null && !(tags.isEmpty())) {
            updateGiftsAndTags(giftCertificateId, tags);
        }

        return giftCertificateId;
    }

    /**
     * Updates a {@link GiftCertificate} object in a database by its id or throws {@link ResourceNotFoundException} if the object
     * with such id doesn't exist or {@link IllegalArgumentException} if the parameter object's price or duration values are lower than 0.
     *
     * @param id              - the {@link GiftCertificate} object's id that is to be updated in a database.
     * @param giftCertificate - the {@link GiftCertificate} object which has the new values for update in a database.
     */
    @Transactional
    public void updateById(long id, GiftCertificate giftCertificate) {
        Optional<GiftCertificate> oldGiftCertificate = giftCertificateDao.getById(id);
        oldGiftCertificate.orElseThrow(
                () -> new ResourceNotFoundException(GenericExceptionMessageConstants.RESOURCE_NOT_FOUND, GIFT_CERTIFICATE));

        giftCertificateValidator.validateForUpdate(giftCertificate);

        LocalDateTime lastUpdateDate = DateTimeUtils.nowOfPattern(DATE_TIME_PATTERN);
        giftCertificate.setLastUpdateDate(lastUpdateDate);

        Map<String, String> fieldNameValueForUpdate = toMapNotNullFields(giftCertificate);
        giftCertificateDao.update(id, fieldNameValueForUpdate);

        List<Tag> tags = giftCertificate.getTags();

        if (tags != null && !(tags.isEmpty())) {
            updateGiftsAndTags(id, tags);
        }
    }

    /**
     * Deletes a {@link GiftCertificate} object in a database by its id or throws {@link ResourceNotFoundException} if the object
     * with such id doesn't exist.
     *
     * @param id - the {@link GiftCertificate} object's id that is to be deleted in a database.
     */
    public void delete(long id) {
        Optional<GiftCertificate> optionalGiftCertificate = giftCertificateDao.getById(id);
        optionalGiftCertificate.orElseThrow(
                () -> new ResourceNotFoundException(GenericExceptionMessageConstants.RESOURCE_NOT_FOUND, GIFT_CERTIFICATE));

        giftCertificateDao.delete(id);
    }

    private List<GiftCertificate> returnWithOffset(List<GiftCertificate> giftCertificates, int offset) {
        return giftCertificates
                .stream()
                .skip(offset)
                .collect(Collectors.toList());
    }

    private List<GiftCertificate> returnWithLimit(List<GiftCertificate> giftCertificates, int limit) {
        return giftCertificates
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<GiftCertificate> returnGiftCertificatesLinkedToTag(Tag tag, List<GiftCertificate> giftCertificates) {
        long tagId = tag.getId();
        List<Long> linkedCertificateIds = giftAndTagDao.getCertificateIdsByTagId(tagId);

        return giftCertificates
                .stream()
                .filter(giftCertificate -> linkedCertificateIds.contains(giftCertificate.getId()))
                .collect(Collectors.toList());
    }

    private void setGiftCertificateTags(GiftCertificate giftCertificate) {
        List<Long> tagIds = giftAndTagDao.getTagIdsByCertificateId(giftCertificate.getId());
        List<Tag> tags = tagService.getTagsByListOfIds(tagIds);
        giftCertificate.setTags(tags);
    }

    private void setGiftCertificateTags(List<GiftCertificate> giftCertificates) {
        for (GiftCertificate giftCertificate : giftCertificates) {
            setGiftCertificateTags(giftCertificate);
        }
    }

    private void updateGiftsAndTags(long giftCertificateId, List<Tag> tags) {
        List<Long> linkedTagIdsBeforeUpdate = giftAndTagDao.getTagIdsByCertificateId(giftCertificateId);
        List<Long> tagIdsAfterUpdate = tagService.createTagsIfNotCreated(tags);

        tagIdsAfterUpdate.stream()
                .filter(tagId -> !linkedTagIdsBeforeUpdate.contains(tagId))
                .forEach(tagId -> giftAndTagDao.create(giftCertificateId, tagId));

        linkedTagIdsBeforeUpdate.stream()
                .filter(linkedTagIdBeforeUpdate -> !tagIdsAfterUpdate.contains(linkedTagIdBeforeUpdate))
                .forEach(linkedTagIdBeforeUpdate -> giftAndTagDao.delete(giftCertificateId, linkedTagIdBeforeUpdate));
    }

    private Map<String, String> toMapNotNullFields(GiftCertificate giftCertificate) {
        Map<String, String> presentFields = new HashMap<>();

        String name = giftCertificate.getName();
        String description = giftCertificate.getDescription();
        Integer price = giftCertificate.getPrice();
        Long duration = giftCertificate.getDuration();
        LocalDateTime createDate = giftCertificate.getCreateDate();
        LocalDateTime lastUpdateDate = giftCertificate.getLastUpdateDate();

        if (name != null) {
            presentFields.put(GiftCertificateConstants.NAME, name);
        }
        if (description != null) {
            presentFields.put(GiftCertificateConstants.DESCRIPTION, description);
        }
        if (price != null) {
            presentFields.put(GiftCertificateConstants.PRICE, price.toString());
        }
        if (duration != null) {
            presentFields.put(GiftCertificateConstants.DURATION, duration.toString());
        }
        if (createDate != null) {
            presentFields.put(GiftCertificateConstants.CREATE_DATE, createDate.toString());
        }
        if (lastUpdateDate != null) {
            presentFields.put(GiftCertificateConstants.LAST_UPDATE_DATE, lastUpdateDate.toString());
        }

        return presentFields;
    }
}
