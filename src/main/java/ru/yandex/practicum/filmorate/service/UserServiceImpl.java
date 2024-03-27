package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.ecxeption.NotFoundException;
import ru.yandex.practicum.filmorate.ecxeption.RepeatException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserStorage userStorage;

    @Override
    public User create(User user) {
        return userStorage.create(user);
    }

    @Override
    public User update(User user) {
        return userStorage.update(user);
    }

    @Override
    public User getById(int id) {
        return userStorage.getById(id);
    }

    @Override
    public List<User> findAll() {
        return userStorage.findAll();
    }

    @Override
    public User addFriend(int userId, int friendId) {
        if (userStorage.getById(userId) == null) {
            throw new NotFoundException("No User with such id");
        }
        if (userStorage.getById(friendId) == null) {
            throw new NotFoundException("No friend with such id");
        }
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);
        if (user.getFriendsId().contains(friendId) || friend.getFriendsId().contains(userId)) {
            throw new RepeatException("Users already friends");
        }
        user.addFriend(friendId);
        friend.addFriend(userId);
        userStorage.update(user);
        userStorage.update(friend);
        return friend;
    }

    @Override
    public User removeFriend(int userId, int friendId) {
        if (userStorage.getById(userId) == null) {
            throw new NotFoundException("No User with such id");
        }
        if (userStorage.getById(friendId) == null) {
            throw new NotFoundException("No friend with such id");
        }
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);
        if (user.getFriendsId().isEmpty()) {
            throw new NoSuchElementException("User has no friends, yet");
        }
        if (!user.getFriendsId().contains(friendId) || !friend.getFriendsId().contains(userId)) {
            throw new RepeatException("Users are not friends");
        }
        user.removeFriend(friendId);
        friend.removeFriend(userId);
        userStorage.update(user);
        userStorage.update(friend);
        return friend;
    }

    @Override
    public List<User> getSameFriends(int userId, int friendId) {
        if (userStorage.getById(userId) == null) {
            throw new NotFoundException("No User with such id");
        }
        if (userStorage.getById(friendId) == null) {
            throw new NotFoundException("No friend with such id");
        }
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);
        Set<Integer> userFriendsIds = user.getFriendsId();
        Set<Integer> friendFriendsIds = friend.getFriendsId();
        Set<Integer> sameIds = new HashSet<>();
        for (Integer userFriendsId : userFriendsIds) {
            if (friendFriendsIds.contains(userFriendsId)) {
                sameIds.add(userFriendsId);
            }
        }
        return userStorage.findAll().stream()
                .filter(u -> sameIds.contains(u.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getFriends(int userId) {
        if (userStorage.getById(userId) == null) {
            throw new NotFoundException("No User with such id");
        }
        Set<Integer> friendsIds = userStorage.getById(userId).getFriendsId();
        return userStorage.findAll().stream()
                .filter(user -> friendsIds.contains(user.getId()))
                .collect(Collectors.toList());
    }
}
