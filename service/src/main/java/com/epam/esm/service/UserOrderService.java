package com.epam.esm.service;

import com.epam.esm.constant.GenericExceptionMessageConstants;
import com.epam.esm.dao.UserOrderDao;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.UserOrder;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.util.DateTimeUtils;
import com.epam.esm.validator.UserOrderValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserOrderService implements com.epam.esm.service.Service<UserOrder> {

    private final UserOrderDao userOrderDao;
    private final UserOrderValidator userOrderValidator;
    private final UserService userService;
    private final GiftCertificateService giftCertificateService;

    private static final String USER_ORDER = "User Order";
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    @Autowired
    public UserOrderService(UserOrderDao userOrderDao,
                            UserOrderValidator userOrderValidator,
                            UserService userService,
                            GiftCertificateService giftCertificateService) {
        this.userOrderDao = userOrderDao;
        this.userOrderValidator = userOrderValidator;
        this.userService = userService;
        this.giftCertificateService = giftCertificateService;
    }

    public List<UserOrder> getAll(int limit, int offset) {
        if (limit < 0 || offset < 0) {
            throw new IllegalArgumentException(GenericExceptionMessageConstants.NEGATIVE_VALUE_PROHIBITED);
        }

        return userOrderDao.getAll(limit, offset);
    }

    public List<UserOrder> getAllByUserId(long userId, int limit, int offset) {
        if (limit < 0 || offset < 0) {
            throw new IllegalArgumentException(GenericExceptionMessageConstants.NEGATIVE_VALUE_PROHIBITED);
        }

        return userOrderDao.getAllByUserId(userId, limit, offset);
    }

    public UserOrder getById(long id) {
        Optional<UserOrder> optionalOrder = userOrderDao.getById(id);
        return optionalOrder.orElseThrow(() -> new ResourceNotFoundException(GenericExceptionMessageConstants.RESOURCE_NOT_FOUND, USER_ORDER));
    }

    public long create(UserOrder userOrder) {
        userOrderValidator.validateForCreation(userOrder);
        userService.getById(userOrder.getUserId()); // check that user exists
        GiftCertificate giftCertificate = giftCertificateService.getById(userOrder.getGiftCertificateId());

        Integer userOrderCost = giftCertificate.getPrice();
        userOrder.setCost(userOrderCost);

        LocalDateTime purchaseTimestamp = DateTimeUtils.nowOfPattern(DATE_TIME_PATTERN);
        userOrder.setPurchaseTimestamp(purchaseTimestamp);

        return userOrderDao.create(userOrder);
    }

    public void delete(long id) {
        Optional<UserOrder> optionalUserOrder = userOrderDao.getById(id);
        optionalUserOrder.orElseThrow(() -> new ResourceNotFoundException(GenericExceptionMessageConstants.RESOURCE_NOT_FOUND, USER_ORDER));

        userOrderDao.delete(id);
    }
}
