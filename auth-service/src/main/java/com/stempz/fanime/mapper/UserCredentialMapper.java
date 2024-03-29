package com.stempz.fanime.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.stempz.fanime.dto.AuthenticationResponseDto;
import com.stempz.fanime.dto.UserCredentialDto;
import com.stempz.fanime.model.UserCredential;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserCredentialMapper {

  @BeanMapping(ignoreByDefault = true)
  @Mapping(source = "email", target = "email")
  @Mapping(source = "username", target = "_username")
  UserCredential toUserCredential(UserCredentialDto userCredentialDto);

  default AuthenticationResponseDto mapToAuthenticationResponseDto(
      UserCredential userCredential, String jwt, boolean authenticated) {
    return AuthenticationResponseDto.builder()
        .authenticated(authenticated)
        .id(userCredential.getId())
        .email(userCredential.getEmail())
        .username(userCredential.get_username())
        .jwt(jwt)
        .role(userCredential.getRole())
        .verified(userCredential.isVerified())
        .build();
  }
}
