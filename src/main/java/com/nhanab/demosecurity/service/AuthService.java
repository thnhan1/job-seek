package com.nhanab.demosecurity.service;

import com.nhanab.demosecurity.dto.JwtResponse;
import com.nhanab.demosecurity.dto.SignupRequest;
import com.nhanab.demosecurity.dto.StudentSignUpRequest;
import com.nhanab.demosecurity.dto.UserProfileDto;
import com.nhanab.demosecurity.entity.*;
import com.nhanab.demosecurity.exception.InfoAlreadyRegisteredException;
import com.nhanab.demosecurity.repository.RoleRepository;
import com.nhanab.demosecurity.repository.StudentProfileRepository;
import com.nhanab.demosecurity.repository.UserRepository;
import com.nhanab.demosecurity.security.UserDetailsImpl;
import com.nhanab.demosecurity.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final StudentProfileRepository studentProfileRepository;

    public void addNewAdminAccount(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new InfoAlreadyRegisteredException("Student with id " + request.getUsername() + " already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new InfoAlreadyRegisteredException("Email already exists");
        }

        String username = request.getUsername();
        String password = request.getPassword();

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(request.getEmail());
        Role role = roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow();
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        user.setRoles(roles);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void addNewUser(StudentSignUpRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getStudentId())) {
            throw new InfoAlreadyRegisteredException("Student with id " + signUpRequest.getStudentId() + " already exists");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
           throw new InfoAlreadyRegisteredException("Email already exists");
        }

        User user = new User(signUpRequest.getStudentId(), signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()));

        StudentProfile studentProfile = new StudentProfile();
        studentProfile.setUser(user);
        studentProfile.setEmail(signUpRequest.getEmail());
        studentProfile.setMajor(signUpRequest.getMajor());
        studentProfile.setStudentId(signUpRequest.getStudentId());
        studentProfile.setFullName(signUpRequest.getFullName());
        user.setCreatedAt(LocalDateTime.now());
        Set<Role> roles = new HashSet<>();
        Role role = roleRepository.findByName(ERole.ROLE_STUDENT).orElseThrow(() -> new RuntimeException("Role not found"));
        roles.add(role);
        user.setRoles(roles);
        user.setStudentProfile(studentProfile);

        userRepository.save(user);
    }

    public JwtResponse login(String username, String password) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();


        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        try {
            String jwt = jwtUtils.generateToken(userDetails.getId(), userDetails.getUsername(), roles);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

            return new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles);
        } catch (Exception e) {
            e.printStackTrace();

        }
        throw new RuntimeException("unexpected error");
    }

    public UserProfileDto getUserProfile() {
        if (!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails)) {
            throw
                    new RuntimeException("unexpected error");
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        if (userDetails ==  null) {
            throw new RuntimeException("user not logged in");
        }

        Long id = userDetails.getId();


        UserProfileDto userProfileDto = new UserProfileDto();
        if (userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()).contains(ERole.ROLE_STUDENT.name())) {
            StudentProfile studentProfile = studentProfileRepository.findByUser_Id(id)
                    .orElseThrow(() -> new RuntimeException("student with id " + id + " not found"));
            userProfileDto.setEmail(studentProfile.getEmail());
            userProfileDto.setFullName(studentProfile.getFullName());
            userProfileDto.setMajor(studentProfile.getMajor().toString().replaceAll("_", " "));
            userProfileDto.setStudentId(studentProfile.getStudentId());
            userProfileDto.setId(id);
            userProfileDto.setUsername(userDetails.getUsername());
            String role = userDetails.getAuthorities().iterator().next().getAuthority();
            userProfileDto.setRole(role);
            return userProfileDto;
        }
        userProfileDto.setEmail(userDetails.getEmail());
        userProfileDto.setRole(userDetails.getAuthorities().iterator().next().getAuthority());
        userProfileDto.setId(id);
        userProfileDto.setUsername(userDetails.getUsername());
        return userProfileDto;
    }


}