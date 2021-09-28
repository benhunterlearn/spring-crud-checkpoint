package com.benhunterlearn.springcrudcheckpoint.controller;

import com.benhunterlearn.springcrudcheckpoint.model.AuthenticatedUser;
import com.benhunterlearn.springcrudcheckpoint.model.Count;
import com.benhunterlearn.springcrudcheckpoint.model.User;
import com.benhunterlearn.springcrudcheckpoint.model.UserDto;
import com.benhunterlearn.springcrudcheckpoint.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("")
    public Iterable<User> getAllUsers() {
        return this.repository.findAll();
    }

    @PostMapping("")
    public User postCreate(@RequestBody User user) {
        return this.repository.save(user);
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return this.repository.findById(id);
    }

    @PatchMapping("/{id}")
    public User patchUserByIdToUpdateFields(@PathVariable Long id, @RequestBody UserDto userDto) {
        User currentUser = this.repository.findById(id).orElseThrow();
        if (userDto.getEmail() != null) {
            currentUser.setEmail(userDto.getEmail());
        }
        if (userDto.getPassword() != null) {
            currentUser.setPassword(userDto.getPassword());
        }
        this.repository.save(currentUser);
        return currentUser;
    }

    @DeleteMapping("/{id}")
    public Count deleteUserByIdRendersCountOfUsersInRepository(@PathVariable Long id) {
        this.repository.deleteById(id);
        return new Count(this.repository.count());
    }

    @PostMapping("/authenticate")
    public AuthenticatedUser postAuthenticatesUserByEmailAndPassword(@RequestBody UserDto userDto) {
        // lookup user by email
        User userFromRepository = this.repository.findUserByEmail(userDto.getEmail());
        AuthenticatedUser authenticated = new AuthenticatedUser();
        // if passwords match
        if (userDto.getPassword().equals(userFromRepository.getPassword())) {
            authenticated.setAuthenticated(true);
            authenticated.setUser(userFromRepository);
        } else {
            authenticated.setAuthenticated(false);
        }
        return authenticated;
    }
}
