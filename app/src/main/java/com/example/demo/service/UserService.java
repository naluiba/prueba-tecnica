package com.example.demo.service;

import com.example.demo.dto.request.PhoneRequest;
import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.model.Phone;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Value("${encryption.service.secretKey}")
    String secretKey;
    @Autowired
    private JwtService jwtService;
    public UserResponse createUser(UserRequest userRequest) throws Exception {

        Optional<User> existingUser = userRepository.findByEmail(userRequest.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("User with the email " + userRequest.getEmail() + " already exists.");
        }

        User user = new User();
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(EncryptionService.encrypt(userRequest.getPassword(),secretKey));
        user.setId(UUID.randomUUID());
        user.setCreated(LocalDateTime.now());
        user.setLastLogin(null);
        user.setPhones(this.createPhones(userRequest.getPhones()));
        user.setToken(jwtService.generateToken(userRequest.getEmail()));
        user.setActive(true);

        User savedUser = userRepository.save(user);

        return new UserResponse(
                userRequest,
                savedUser.getId(),
                savedUser.getLastLogin(),
                savedUser.getCreated(),
                savedUser.getToken(),
                savedUser.isActive()
        );
    }

    public List<Phone> createPhones(List<PhoneRequest> phones){
        List<Phone> phonesResult = new ArrayList<>();
        for (PhoneRequest elem : phones) {
            Phone p = new Phone();
            p.setNumber(elem.getNumber());
            p.setCityCode(elem.getCityCode());
            p.setCountryCode(elem.getCountryCode());
            phonesResult.add(p);
        }
        return phonesResult;
    }

    public UserResponse getUser(String token) throws Exception {
        String jwtToken = token.substring(7);
        String username = jwtService.extractEmail(jwtToken);

        Optional<User> userOptional = userRepository.findByEmail(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            UserRequest userRequest = new UserRequest();
            userRequest.setEmail(user.getEmail());
            userRequest.setName(user.getName());
            userRequest.setPassword(EncryptionService.decrypt(user.getPassword(),secretKey));

            String newToken = jwtService.generateToken(user.getEmail());
            user.setToken(newToken);
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            return new UserResponse(
                    userRequest,
                    user.getId(),
                    user.getLastLogin(),
                    user.getCreated(),
                    user.getToken(),
                    user.isActive()
            );
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }

}

