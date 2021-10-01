package com.epam.esm.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GiftAndTagDao {

    private final JdbcTemplate jdbcTemplate;

    private static final String GET_BY_CERTIFICATE_ID = "SELECT TAG_ID FROM GIFT_AND_TAG WHERE CERTIFICATE_ID = ?";
    private static final String GET_BY_TAG_ID = "SELECT CERTIFICATE_ID FROM GIFT_AND_TAG WHERE TAG_ID = ?";
    private static final String CREATE = "INSERT INTO GIFT_AND_TAG(CERTIFICATE_ID, TAG_ID) VALUES(?, ?)";
    private static final String DELETE = "DELETE FROM GIFT_AND_TAG WHERE CERTIFICATE_ID = ? AND TAG_ID = ?";

    @Autowired
    public GiftAndTagDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Long> getTagIdsByCertificateId(long certificateId) {
        return jdbcTemplate.queryForList(GET_BY_CERTIFICATE_ID, Long.class, certificateId);
    }

    public List<Long> getCertificateIdsByTagId(Long tagId) {
        return jdbcTemplate.queryForList(GET_BY_TAG_ID, Long.class, tagId);
    }

    public void create(Long certificateId, Long tagId) {
        jdbcTemplate.update(CREATE, certificateId, tagId);
    }

    public void delete(Long certificateId, Long tagId) {
        jdbcTemplate.update(DELETE, certificateId, tagId);
    }
}
