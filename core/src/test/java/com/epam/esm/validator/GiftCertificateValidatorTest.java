package com.epam.esm.validator;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.exception.RequiredFieldMissingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class GiftCertificateValidatorTest {

    private final GiftCertificateValidator validator = new GiftCertificateValidator();

    private static final String NAME = "someName";
    private static final String DESCRIPTION = "someDescription";
    private static final Integer POSITIVE_PRICE = 100;
    private static final Integer NEGATIVE_PRICE = -50;
    private static final Long POSITIVE_DURATION = 10L;
    private static final Long NEGATIVE_DURATION = -1L;

    @Test
    public void testShouldWorkCorrectlyWhenAllFieldsAreFilledForCreation() {
        GiftCertificate giftCertificate = new GiftCertificate();
        giftCertificate.setName(NAME);
        giftCertificate.setDescription(DESCRIPTION);
        giftCertificate.setPrice(POSITIVE_PRICE);
        giftCertificate.setDuration(POSITIVE_DURATION);

        validator.validateForCreation(giftCertificate);
    }

    @Test
    public void testShouldThrowExceptionWhenNoFieldsAreFilledForCreation() {
        assertThrows(RequiredFieldMissingException.class, () -> {
            GiftCertificate giftCertificate = new GiftCertificate();
            validator.validateForCreation(giftCertificate);
        });
    }

    @Test
    public void testShouldThrowExceptionWhenNameFieldNotFilledForCreation() {
        assertThrows(RequiredFieldMissingException.class, () -> {
            GiftCertificate giftCertificate = new GiftCertificate();
            giftCertificate.setDescription(DESCRIPTION);
            giftCertificate.setPrice(POSITIVE_PRICE);
            giftCertificate.setDuration(POSITIVE_DURATION);

            validator.validateForCreation(giftCertificate);
        });
    }

    @Test
    public void testShouldThrowExceptionWhenDescriptionFieldNotFilledForCreation() {
        assertThrows(RequiredFieldMissingException.class, () -> {
            GiftCertificate giftCertificate = new GiftCertificate();
            giftCertificate.setName(NAME);
            giftCertificate.setPrice(POSITIVE_PRICE);
            giftCertificate.setDuration(POSITIVE_DURATION);

            validator.validateForCreation(giftCertificate);
        });
    }

    @Test
    public void testShouldThrowExceptionWhenPriceFieldNotFilledForCreation() {
        assertThrows(RequiredFieldMissingException.class, () -> {
            GiftCertificate giftCertificate = new GiftCertificate();
            giftCertificate.setName(NAME);
            giftCertificate.setDescription(DESCRIPTION);
            giftCertificate.setDuration(POSITIVE_DURATION);

            validator.validateForCreation(giftCertificate);
        });
    }

    @Test
    public void testShouldThrowExceptionWhenDurationFieldNotFilledForCreation() {
        assertThrows(RequiredFieldMissingException.class, () -> {
            GiftCertificate giftCertificate = new GiftCertificate();
            giftCertificate.setName(NAME);
            giftCertificate.setDescription(DESCRIPTION);
            giftCertificate.setPrice(POSITIVE_PRICE);

            validator.validateForCreation(giftCertificate);
        });
    }

    @Test
    public void testShouldThrowExceptionWhenPriceIsNegativeForCreation() {
        assertThrows(IllegalArgumentException.class, () -> {
            GiftCertificate giftCertificate = new GiftCertificate();
            giftCertificate.setName(NAME);
            giftCertificate.setDescription(DESCRIPTION);
            giftCertificate.setPrice(NEGATIVE_PRICE);
            giftCertificate.setDuration(POSITIVE_DURATION);

            validator.validateForCreation(giftCertificate);
        });
    }

    @Test
    public void testShouldWorkCorrectlyWhenPriceIsPositiveForUpdating() {
        GiftCertificate giftCertificate = new GiftCertificate();
        giftCertificate.setPrice(50);

        validator.validateForUpdate(giftCertificate);
    }

    @Test
    public void testShouldThrowExceptionWhenPriceIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            GiftCertificate giftCertificate = new GiftCertificate();
            giftCertificate.setPrice(-50);

            validator.validateForUpdate(giftCertificate);
        });
    }

    @Test
    public void testShouldWorkCorrectlyWhenDurationIsPositive() {
        GiftCertificate giftCertificate = new GiftCertificate();
        giftCertificate.setDuration(POSITIVE_DURATION);

        validator.validateForUpdate(giftCertificate);
    }

    @Test
    public void testShouldThrowExceptionWhenDurationIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            GiftCertificate giftCertificate = new GiftCertificate();
            giftCertificate.setDuration(NEGATIVE_DURATION);

            validator.validateForUpdate(giftCertificate);
        });
    }
}
