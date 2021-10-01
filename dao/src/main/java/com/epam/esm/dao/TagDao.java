package com.epam.esm.dao;

import com.epam.esm.dto.TagCostDto;
import com.epam.esm.entity.Tag;
import com.epam.esm.mapper.TagRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class TagDao implements Dao<Tag> {

    private final JdbcTemplate jdbcTemplate;
    private final TagRowMapper tagRowMapper;

    private static final String GET_ALL = "SELECT ID, NAME FROM TAG LIMIT ? OFFSET ?";
    private static final String GET_BY_ID = "SELECT ID, NAME FROM TAG WHERE ID = ?";
    private static final String GET_BY_NAME = "SELECT ID, NAME FROM TAG WHERE NAME = ?";
    private static final String GET_TAGS_WITH_COST_SUM_OF_ORDERS =
            " SELECT t.*, SUM(cost) costSum FROM tag t "
                    + " INNER JOIN gift_and_tag gt on t.id = gt.tag_id "
                    + " INNER JOIN user_order uo on gt.certificate_id = uo.gift_certificate_id "
                    + " WHERE uo.user_id = ? "
                    + " GROUP BY t.id ";
    private static final String CREATE = "INSERT INTO TAG(NAME) VALUES(?)";
    private static final String DELETE = "DELETE FROM TAG WHERE ID=?";

    private static final int NAME_POSITION = 1;

    @Autowired
    public TagDao(JdbcTemplate jdbcTemplate, TagRowMapper tagRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.tagRowMapper = tagRowMapper;
    }

    public List<Tag> getAll(int limit, int offset) {
        return jdbcTemplate.query(GET_ALL, tagRowMapper, limit, offset);
    }

    public Optional<Tag> getById(long id) {
        List<Tag> entities = jdbcTemplate.query(GET_BY_ID, tagRowMapper, id);
        return getSingleFoundEntity(entities);
    }

    public Optional<Tag> getByName(String name) {
        List<Tag> entities = jdbcTemplate.query(GET_BY_NAME, tagRowMapper, name);
        return getSingleFoundEntity(entities);
    }

    public List<TagCostDto> getTagsWithCostSumsOfUserOrders(long userId) {
        return jdbcTemplate.query(GET_TAGS_WITH_COST_SUM_OF_ORDERS, new BeanPropertyRowMapper<>(TagCostDto.class), userId);
    }

    public long create(Tag tag) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(NAME_POSITION, tag.getName());
            return preparedStatement;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public void delete(long id) {
        jdbcTemplate.update(DELETE, id);
    }
}
