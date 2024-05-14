package ru.yandex.practicum.filmorate.repository.mapper;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.Set;

@Component
@AllArgsConstructor
public class UserRowMapper {

    private final JdbcTemplate template;

    public RowMapper<User> getMapperWithFriendsSet() {
        return ((rs, rowNum) ->
                new User()
                        .setId(rs.getInt("id"))
                        .setName(rs.getString("name"))
                        .setEmail(rs.getString("email"))
                        .setLogin(rs.getString("login"))
                        .setBirthday(rs.getDate("birthday").toLocalDate())
                        .setFriends(getFriendsSetFromDb(rs.getInt("id"))));
    }

    private Set<Integer> getFriendsSetFromDb(int id) {
        return new HashSet<>(template.queryForList("SELECT following_id " +
                "FROM follows " +
                "WHERE followed_id = ?", Integer.class, id));
    }
}
