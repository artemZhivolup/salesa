package com.salesa.service;

import com.salesa.entity.Feedback;
import com.salesa.entity.User;

import java.util.List;

public interface UserService {
    User get(int userId);
    List<Feedback> getByUserId(int userId);
    int save(User user);
    User get(String email);
    void updateUsersDislike(User user);
    void updateUserType(User user);
    void deleteUser(int userId);
    void update(User user);
}
