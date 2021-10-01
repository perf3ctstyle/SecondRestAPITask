package com.epam.esm.validator;

import com.epam.esm.constant.GenericExceptionMessageConstants;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.exception.RequiredFieldMissingException;
import org.apache.commons.lang3.StringUtils;

public class GiftCertificateValidator {

    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String PRICE = "price";
    private static final String DURATION = "duration";

    public void validateForCreation(GiftCertificate giftCertificate) {
        String name = giftCertificate.getName();
        String description = giftCertificate.getDescription();
        Integer price = giftCertificate.getPrice();
        Long duration = giftCertificate.getDuration();

        if (name == null) {
            throw new RequiredFieldMissingException(GenericExceptionMessageConstants.REQUIRED_FIELD_MISSING, NAME);
        }
        if (description == null) {
            throw new RequiredFieldMissingException(GenericExceptionMessageConstants.REQUIRED_FIELD_MISSING, DESCRIPTION);
        }
        if (StringUtils.isWhitespace(name) || StringUtils.isWhitespace(description)) {
            throw new IllegalArgumentException(GenericExceptionMessageConstants.EMPTY_VALUE);
        }
        if (price == null) {
            throw new RequiredFieldMissingException(GenericExceptionMessageConstants.REQUIRED_FIELD_MISSING, PRICE);
        }
        if (duration == null) {
            throw new RequiredFieldMissingException(GenericExceptionMessageConstants.REQUIRED_FIELD_MISSING, DURATION);
        }
        if (price <= 0 || duration <= 0) {
            throw new IllegalArgumentException(GenericExceptionMessageConstants.NEGATIVE_VALUE_PROHIBITED);
        }
    }

    public void validateForUpdate(GiftCertificate giftCertificate) {
        String name = giftCertificate.getName();
        String description = giftCertificate.getDescription();
        Integer price = giftCertificate.getPrice();
        Long duration = giftCertificate.getDuration();

        if (StringUtils.isWhitespace(name) || StringUtils.isWhitespace(description)) {
            throw new IllegalArgumentException(GenericExceptionMessageConstants.EMPTY_VALUE);
        }
        if ((price != null && price <= 0) || (duration != null && duration <= 0))  {
            throw new IllegalArgumentException(GenericExceptionMessageConstants.NEGATIVE_VALUE_PROHIBITED);
        }
    }
}