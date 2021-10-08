package com.epam.esm.constructor;

import com.epam.esm.constant.GiftCertificateConstants;
import com.epam.esm.entity.GiftCertificate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class GiftCertificateObjectConstructor {

    public GiftCertificateObjectConstructor() {
    }

    public Map<String, String> toMapNotNullFields(GiftCertificate giftCertificate) {
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
