package com.stempz.fanime.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.stempz.fanime.dto.AuthenticationRequestDto;
import com.stempz.fanime.dto.AuthenticationResponseDto;
import com.stempz.fanime.dto.UserCredentialDto;
import com.stempz.fanime.exception.UserAlreadyExistsException;
import com.stempz.fanime.exception.UserAlreadyExistsException.FieldType;
import com.stempz.fanime.jwt.JwtService;
import com.stempz.fanime.mapper.UserCredentialMapper;
import com.stempz.fanime.model.UserCredential;
import com.stempz.fanime.model.enums.Role;
import com.stempz.fanime.repository.UserCredentialRepo;
import com.stempz.fanime.service.UserCredentialService;

@Service
@RequiredArgsConstructor
public class UserCredentialServiceImpl implements UserCredentialService {

  private final UserCredentialRepo userCredentialRepo;
  private final UserCredentialMapper userCredentialMapper;

  private final AuthenticationProvider authenticationProvider;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;

  public AuthenticationResponseDto authenticate(AuthenticationRequestDto authDto) {
    UserCredential userCredential = (UserCredential) authenticate(authDto.email(), authDto.password());

    String jwt = jwtService.generateToken(userCredential, userCredential.getId());
    return userCredentialMapper.mapToAuthenticationResponseDto(userCredential, jwt, true);
  }

  private UserDetails authenticate(String username, char[] password) {
    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
        username, String.valueOf(password));
    Authentication authentication = authenticationProvider.authenticate(authToken);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    return (UserDetails) authentication.getPrincipal();
  }

  public AuthenticationResponseDto register(UserCredentialDto userCredentialDto) {
    if (userCredentialRepo.existsByEmailIgnoreCase(userCredentialDto.email())) {
      throw new UserAlreadyExistsException(userCredentialDto.email(), FieldType.EMAIL);
    } else if (userCredentialRepo.existsByUsernameIgnoreCase(userCredentialDto.username())) {
      throw new UserAlreadyExistsException(userCredentialDto.username(), FieldType.USERNAME);
    }

    UserCredential userCredential = userCredentialMapper.toUserCredential(userCredentialDto);
    userCredential.setPassword(encodePassword(userCredentialDto.password()));
    userCredential.setRole(Role.USER);

    userCredential = userCredentialRepo.save(userCredential);

    authenticate(userCredentialDto.email(), userCredentialDto.password());
    String jwt = jwtService.generateToken(userCredential, userCredential.getId());
    return userCredentialMapper.mapToAuthenticationResponseDto(userCredential, jwt, true);
  }

  public AuthenticationResponseDto validate(HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
      return new AuthenticationResponseDto(false);
    }

    String token = jwtService.extractBearerToken(request).get();
    UserCredential userCredential = (UserCredential) authentication.getPrincipal();
    return userCredentialMapper.mapToAuthenticationResponseDto(userCredential, token, true);
  }

  private String encodePassword(char[] password) {
    return passwordEncoder.encode(String.valueOf(password));
  }
}
