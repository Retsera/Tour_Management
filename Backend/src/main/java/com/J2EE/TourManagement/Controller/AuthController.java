package com.J2EE.TourManagement.Controller;

import com.J2EE.TourManagement.Model.DTO.*;
import com.J2EE.TourManagement.Model.RestResponse;
import com.J2EE.TourManagement.Model.User;
import com.J2EE.TourManagement.Service.UserSer;
import com.J2EE.TourManagement.Util.SecurityUtil;
import com.J2EE.TourManagement.Util.annotation.ApiMessage;
import com.J2EE.TourManagement.Util.error.InvalidException;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final SecurityUtil securityUtil;
  private final UserSer userService;
  private final PasswordEncoder passwordEncoder;

  @Value("${mt.jwt.refresh-token-validity-in-seconds}")
  private long refreshTokenExpiration;

  public AuthController(
      AuthenticationManagerBuilder authenticationManagerBuilder,
      SecurityUtil securityUtil, UserSer userService,
      PasswordEncoder passwordEncoder) {
    this.authenticationManagerBuilder = authenticationManagerBuilder;
    this.securityUtil = securityUtil;
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/login")
  @ApiMessage("Đăng nhập thành công")
  public ResponseEntity<ResLoginDTO> login(@RequestBody LoginDTO loginDTO) {
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(loginDTO.getUsername(),
                                                loginDTO.getPassword());

    // xác thực người dùng => cần viết hàm loadUserByUsername
    Authentication authentication =
        authenticationManagerBuilder.getObject().authenticate(
            authenticationToken);

    SecurityContextHolder.getContext().setAuthentication(authentication);
    ResLoginDTO resLoginDTO = new ResLoginDTO();
    User currentUser = this.userService.getUserByName(loginDTO.getUsername());
    RoleDTO roleDTO = new RoleDTO(currentUser.getRole().getId(),
                                  currentUser.getRole().getNameRole(),
                                  currentUser.getRole().getDescription(),
                                  currentUser.getRole().isStatus());
    ResLoginDTO.UserLogin userLogin =
        new ResLoginDTO.UserLogin(currentUser.getId(), currentUser.getEmail(),
                                  currentUser.getFullname(), roleDTO);

    String access_token = this.securityUtil.createAccessToken(
        authentication.getName(), userLogin);
    resLoginDTO.setAccessToken(access_token);

    resLoginDTO.setUserLogin(userLogin);

    // create refresh Token
    String refreshToken = this.securityUtil.createRefreshToken(
        loginDTO.getUsername(), resLoginDTO);

    // update user
    this.userService.UpdateRefreshToken(refreshToken, loginDTO.getUsername());

    //  set cookies

    ResponseCookie responseCookie =
        ResponseCookie.from("refresh_Token", refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(refreshTokenExpiration)
            .build();
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
        .body(resLoginDTO);
  }

  @GetMapping("/auth/account")
  @ApiMessage("fetch Account")
  public ResponseEntity<?> getAccount() throws InvalidException {
    Optional<String> currentLogin = this.securityUtil.getCurrentUserLogin();

    if (currentLogin.isEmpty()) {
      throw new InvalidException(
          "Người dùng chưa đăng nhập hoặc token không hợp lệ.");
    }

    String email = currentLogin.get();
    User currentUser = this.userService.getUserByName(email);

    if (currentUser == null) {
      throw new InvalidException(
          "Không tìm thấy thông tin người dùng với email: " + email);
    }

    if (currentUser.getRole() == null) {
      throw new InvalidException("Người dùng chưa được gán quyền (role).");
    }
    RoleDTO roleDTO = new RoleDTO(currentUser.getRole().getId(),
                                  currentUser.getRole().getNameRole(),
                                  currentUser.getRole().getDescription(),
                                  currentUser.getRole().isStatus());
    ResLoginDTO.UserLogin userLogin =
        new ResLoginDTO.UserLogin(currentUser.getId(), currentUser.getEmail(),
                                  currentUser.getFullname(), roleDTO);
    return ResponseEntity.ok().body(userLogin);
  }

  @GetMapping("/auth/refresh")
  @ApiMessage("get user by refresh toke")
  public ResponseEntity<?>
  getRefreshToken(@CookieValue(name = "refresh_Token") String refresh_token) {
    Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);

    String email = decodedToken.getSubject();

    User user =
        this.userService.getUserByRefreshTokenAnhEmail(refresh_token, email);

    ResLoginDTO resLoginDTO = new ResLoginDTO();
    User currentUser = this.userService.getUserByName(email);
    RoleDTO roleDTO = new RoleDTO(currentUser.getRole().getId(),
                                  currentUser.getRole().getNameRole(),
                                  currentUser.getRole().getDescription(),
                                  currentUser.getRole().isStatus());
    ResLoginDTO.UserLogin userLogin =
        new ResLoginDTO.UserLogin(currentUser.getId(), currentUser.getEmail(),
                                  currentUser.getFullname(), roleDTO);

    String access_token = this.securityUtil.createAccessToken(email, userLogin);
    resLoginDTO.setAccessToken(access_token);

    resLoginDTO.setUserLogin(userLogin);

    // create refresh Token
    String newRefreshToken =
        this.securityUtil.createRefreshToken(email, resLoginDTO);

    // update user
    this.userService.UpdateRefreshToken(newRefreshToken, email);

    //  set cookies
    ResponseCookie responseCookie =
        ResponseCookie.from("refresh_Token", newRefreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(refreshTokenExpiration)
            .build();
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
        .body(resLoginDTO);
  }

  @PostMapping("/auth/logout")
  public ResponseEntity<?> logOut() throws InvalidException {
    String email = SecurityUtil.getCurrentUserLogin().isPresent()
                       ? SecurityUtil.getCurrentUserLogin().get()
                       : "";

    if (email.equals("")) {
      throw new InvalidException("Access Token không hợp lệ. ");
    }

    this.userService.UpdateRefreshToken(null, email);

    ResponseCookie responseCookie = ResponseCookie.from("refresh_Token", null)
                                        .httpOnly(true)
                                        .secure(true)
                                        .path("/")
                                        .maxAge(0)
                                        .build();

    RestResponse<Object> body = new RestResponse<>();
    body.setStatusCode(200);
    body.setError(null);
    body.setMessage("Đăng xuất thành công");
    body.setData(null);

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
        .body(body);
  }

  @PostMapping("/auth/register")
  @ApiMessage("Đăng kí thành Công.")
  public ResponseEntity<?>
  postRegister(@RequestBody @Valid RegisterDTO RegisterDTO)
      throws InvalidException {
    boolean isEmailExist =
        this.userService.isEmailExist(RegisterDTO.getEmail());

    if (isEmailExist) {
      throw new InvalidException("Email đã tồn tại. Vui lòng nhập email khác.");
    }
    if (!RegisterDTO.getPassword().equals(RegisterDTO.getConfirmPassword())) {
      throw new InvalidException("Nhập lại mật khẩu không chính xác.");
    }

    String hashPassword =
        this.passwordEncoder.encode(RegisterDTO.getPassword());
    RegisterDTO.setPassword(hashPassword);

    User newUser = this.userService.convertRegisterDtoToUser(RegisterDTO);
    User user = this.userService.handleSaveUser(newUser);

    CreateUserDTO resUserDTO = this.userService.convertUserToResUserDto(user);
    return ResponseEntity.ok(resUserDTO);
  }

  @GetMapping("/login/oauth2/success")
  public ResponseEntity<ResLoginDTO>
  googleLoginSuccess(@AuthenticationPrincipal OAuth2User principal) {
    ResLoginDTO resLoginDTO = new ResLoginDTO();

    // Giả sử bạn tìm user trong DB bằng email Google
    User currentUser =
        this.userService.getUserByName(principal.getAttribute("email"));
    if (currentUser == null) {
      currentUser = this.userService.createUserFromGoogle(principal);
    }

    RoleDTO roleDTO = new RoleDTO(currentUser.getRole().getId(),
                                  currentUser.getRole().getNameRole(),
                                  currentUser.getRole().getDescription(),
                                  currentUser.getRole().isStatus());

    ResLoginDTO.UserLogin userLogin =
        new ResLoginDTO.UserLogin(currentUser.getId(), currentUser.getEmail(),
                                  currentUser.getFullname(), roleDTO);

    String accessToken =
        this.securityUtil.createAccessToken(currentUser.getEmail(), userLogin);
    resLoginDTO.setAccessToken(accessToken);
    resLoginDTO.setUserLogin(userLogin);

    // refresh token nếu cần
    String refreshToken = this.securityUtil.createRefreshToken(
        currentUser.getEmail(), resLoginDTO);
    this.userService.UpdateRefreshToken(refreshToken, currentUser.getEmail());

    ResponseCookie responseCookie =
        ResponseCookie.from("refresh_Token", refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(refreshTokenExpiration)
            .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
        .body(resLoginDTO);
  }

  
}
