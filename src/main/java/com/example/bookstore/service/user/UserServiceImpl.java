package com.example.bookstore.service.user;

import com.example.bookstore.dto.user.UserRegistrationRequestDto;
import com.example.bookstore.dto.user.UserResponseDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.exception.RegistrationException;
import com.example.bookstore.mapper.UserMapper;
import com.example.bookstore.model.Role;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.role.RoleRepository;
import com.example.bookstore.repository.user.UserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto registrationDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(registrationDto.email())) {
            throw new RegistrationException(
                    String.format("User with email %s already exists",
                    registrationDto.email()));
        }

        User userFromDto = userMapper.toEntity(registrationDto);
        userFromDto.setPassword(passwordEncoder.encode(registrationDto.password()));
        userFromDto.setRoles(Set.of(
                findRoleByName(Role.RoleName.USER),
                findRoleByName(Role.RoleName.MANAGER)));

        return userMapper.toDto(userRepository.save(userFromDto));
    }

    private Role findRoleByName(Role.RoleName roleName) {
        return roleRepository.findByName(roleName).orElseThrow(
                () -> new EntityNotFoundException("Can't find role in database:" + roleName));
    }
}
