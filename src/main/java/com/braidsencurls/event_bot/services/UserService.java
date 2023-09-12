package com.braidsencurls.event_bot.services;

import com.braidsencurls.event_bot.entities.User;

public interface UserService {
    User findByUsername(String username);
    boolean isAdmin(String username);
    void addUser(String username, String role);
    boolean deleteUser(String username);
}
