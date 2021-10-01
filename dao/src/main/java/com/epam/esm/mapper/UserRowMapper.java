package com.epam.esm.mapper;

import com.epam.esm.constant.UserConstants;
import com.epam.esm.entity.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong(UserConstants.ID);
        return new User(id);
    }
}
