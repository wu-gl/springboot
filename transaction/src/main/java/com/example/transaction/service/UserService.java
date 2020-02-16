package com.example.transaction.service;

public interface UserService {
    void addUser(String name, String email, String pwd);

    void deleteUser(String id) throws Exception;
}
