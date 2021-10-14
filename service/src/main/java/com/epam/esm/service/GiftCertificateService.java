package com.epam.esm.service;

import com.epam.esm.constant.GenericExceptionMessageConstants;
import com.epam.esm.dto.SearchInfoDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.DaoException;
import com.epam.esm.exception.RequiredFieldMissingException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.hibernate.GiftAndTagDao;
import com.epam.esm.hibernate.GiftCertificateDao;
import com.epam.esm.util.DateTimeUtils;
import com.epam.esm.validator.GiftCertificateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.epam.esm.constant.GenericConstants.DATE_TIME_PATTERN;

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
    private static final String LAST_UPDATE_DATE = "last_update_date";

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

    /**
     * Returns {@link GiftCertificate} objects from a database without any filtering.
     *
     * @param limit  - a number of {@link GiftCertificate} objects to return
     * @param offset - a number of {@link GiftCertificate} objects to skip when returning
     * @return a {@link List} of {@link GiftCertificate} objects.
     */
    public List<GiftCertificate> getAll(int limit, int offset) {
        checkPaginationParameters(limit, offset);

        List<GiftCertificate> giftCertificates = giftCertificateDao.getAll(limit, offset);
        setGiftCertificateTags(giftCertificates);

        return giftCertificates;
    }

    /**
     * Returns a {@link List} of {@link GiftCertificate} objects from a database.
     *
     * @param searchInfo - an object that contains the partial search and sorting info
     * @param tagNames   - names of {@link Tag} objects which are linked to a {@link GiftCertificate} object.
     * @param limit      - a number of {@link GiftCertificate} objects to return
     * @param offset     - a number of {@link GiftCertificate} objects to skip when returning
     * @return a {@link List} of {@link GiftCertificate} objects.
     */
    public List<GiftCertificate> getGiftCertificates(SearchInfoDto searchInfo, String[] tagNames, int limit, int offset) {
        checkPaginationParameters(limit, offset);

        SearchInfoDto searchInfoToUse = (searchInfo != null) ? searchInfo : new SearchInfoDto();
        String[] tagNamesToUse = (tagNames != null) ? tagNames : new String[0];

        List<GiftCertificate> giftCertificates = giftCertificateDao.getGiftCertificates(searchInfoToUse, tagNamesToUse, limit, offset);
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
     * @return {@link GiftCertificate} object's id which was created in a database.
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
     * @param id                     - the {@link GiftCertificate} object's id that is to be updated in a database.
     * @param fieldAndValueForUpdate - the new values for updating a {@link GiftCertificate} object in a database.
     */
    @Transactional
    public void updateById(long id, Map<String, String> fieldAndValueForUpdate) {
        Optional<GiftCertificate> oldGiftCertificate = giftCertificateDao.getById(id);
        oldGiftCertificate.orElseThrow(
                () -> new ResourceNotFoundException(GenericExceptionMessageConstants.RESOURCE_NOT_FOUND, GIFT_CERTIFICATE));

        giftCertificateValidator.validateForUpdate(fieldAndValueForUpdate);

        LocalDateTime lastUpdateDate = DateTimeUtils.nowOfPattern(DATE_TIME_PATTERN);
        fieldAndValueForUpdate.put(LAST_UPDATE_DATE, lastUpdateDate.toString());

        giftCertificateDao.update(id, fieldAndValueForUpdate);
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

        Map<String, String> fieldNameValueForUpdate = giftCertificate.toMapNotNullFields();
        giftCertificateDao.update(id, fieldNameValueForUpdate);

        List<Tag> tags = giftCertificate.getTags();
        updateGiftsAndTags(id, tags);
    }

    /**
     * Deletes a {@link GiftCertificate} object in a database by its id or throws {@link ResourceNotFoundException} if the object
     * with such id doesn't exist.
     *
     * @param id - the {@link GiftCertificate} object's id that is to be deleted in a database.
     */
    @Transactional
    public void delete(long id) {
        Optional<GiftCertificate> optionalGiftCertificate = giftCertificateDao.getById(id);
        GiftCertificate giftCertificate = optionalGiftCertificate.orElseThrow(
                () -> new ResourceNotFoundException(GenericExceptionMessageConstants.RESOURCE_NOT_FOUND, GIFT_CERTIFICATE));

        giftCertificateDao.delete(giftCertificate);
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
}
