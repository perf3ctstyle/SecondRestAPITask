package com.epam.esm.dao;

import com.epam.esm.entity.User;
import com.epam.esm.mapper.UserRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDao implements Dao<User> {

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    private static final String GET_ALL = "SELECT ID FROM USER LIMIT ? OFFSET ?";
    private static final String GET_BY_ID = "SELECT ID FROM USER WHERE ID = ?";
    private static final String CREATE = "INSERT INTO USER VALUES()";
    private static final String DELETE = "DELETE FROM USER WHERE ID = ?";

    @Autowired
    public UserDao(JdbcTemplate jdbcTemplate, UserRowMapper userRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
    }

    public List<User> getAll(int limit, int offset) {
        return jdbcTemplate.query(GET_ALL, userRowMapper, limit, offset);
    }

    public Optional<User> getById(long id) {
        List<User> entities = jdbcTemplate.query(GET_BY_ID, userRowMapper, id);
        return getSingleFoundEntity(entities);
    }

    public long create(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE, Statement.RETURN_GENERATED_KEYS);
            return preparedStatement;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public void delete(long id) {
        jdbcTemplate.update(DELETE, id);
    }
}
