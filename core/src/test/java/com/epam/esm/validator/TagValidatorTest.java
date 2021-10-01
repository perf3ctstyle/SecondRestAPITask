package com.epam.esm.validator;

import com.epam.esm.entity.Tag;
import com.epam.esm.exception.RequiredFieldMissingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TagValidatorTest {

    private final TagValidator tagValidator = new TagValidator();

    private static final String NAME = "someName";

    @Test
    public void testShouldWorkCorrectlyWhenAllFieldsAreFilledForCreation() {
        Tag tag = new Tag(NAME);

        tagValidator.validateForCreation(tag);
    }

    @Test
    public void testShouldThrowExceptionWhenNameFieldNotFilledForCreation() {
        assertThrows(RequiredFieldMissingException.class, () -> {
            Tag tag = new Tag();

            tagValidator.validateForCreation(tag);
        });
    }
}
