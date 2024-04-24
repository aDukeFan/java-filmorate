package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository repository;

    @Override
    public User create(User user) {
        return repository.create(user);
    }

    @Override
    public User update(User user) {
        return repository.update(user);
    }

    @Override
    public User getById(int id) {
        return repository.getById(id);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public User addFollow(int followingId, int followedId) {
        return repository.addFollow(followingId, followedId);
    }

    @Override
    public User removeFollow(int followingId, int followedId) {
        return repository.removeFollowing(followingId, followedId);
    }
//        User user = userStorage.getById(userId);
//        User friend = userStorage.getById(friendId);
//        if (user.getFollowersIds().isEmpty()) {
//            throw new NoSuchElementException("User has no friends, yet");
//        }
//        if (!user.getFollowersIds().contains(friendId) || !friend.getFollowersIds().contains(userId)) {
//            throw new RepeatException("Users are not friends");
//        }
//        user.removeFriend(friendId);
//        friend.removeFriend(userId);
//        return friend;
//    }

    @Override
    public List<User> getSameFriends(int userId, int friendId) {
        return repository.getSameFollowers(userId, friendId);
    }

//        User user = userStorage.getById(userId);
//        User friend = userStorage.getById(friendId);
//        List<Integer> sameIds = user.getFollowersIds().stream()
//                .filter(friend.getFollowersIds()::contains)
//                .collect(Collectors.toList());
//        return userStorage.findAll().stream()
//                .filter(u -> sameIds.contains(u.getId()))
//                .collect(Collectors.toList());
//    }

    @Override
    public List<User> getFriends(int userId) {
        return repository.getFollowers(userId);
    }
//        User user = userStorage.getById(userId);
//        return userStorage.findAll().stream()
//                .filter(u -> user.getFollowersIds().contains(u.getId()))
//                .collect(Collectors.toList());

}
