package ru.yandex.practicum.filmorate.util;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class UserRowMapper {

    private final JdbcTemplate template;

    public UserRowMapper(JdbcTemplate template) {
        this.template = template;
    }

    private Set<Integer> setFriendsListFromDb(int id) {
        Set<Integer> friends = new LinkedHashSet<>();
        SqlRowSet sqlRowSet = template.queryForRowSet(
                "select following_id " +
                        "from follows " +
                        "where followed_id = ?", id);
        while (sqlRowSet.next()) {
            friends.add(sqlRowSet.getInt("following_id"));
        }
        return friends;
    }

    public RowMapper<User> userRowMapper() {
        return ((rs, rowNum) ->
                new User()
                        .setId(rs.getInt("id"))
                        .setName(rs.getString("name"))
                        .setEmail(rs.getString("email"))
                        .setLogin(rs.getString("login"))
                        .setBirthday(rs.getDate("birthday").toLocalDate())
                        .setFriends(setFriendsListFromDb(rs.getInt("id"))));
    }
}
