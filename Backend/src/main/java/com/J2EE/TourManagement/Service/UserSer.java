package com.J2EE.TourManagement.Service;

import com.J2EE.TourManagement.Model.DTO.CreateUserDTO;
import com.J2EE.TourManagement.Model.DTO.Meta;
import com.J2EE.TourManagement.Model.DTO.RegisterDTO;
import com.J2EE.TourManagement.Model.DTO.ResultPaginationDTO;
import com.J2EE.TourManagement.Model.DTO.UserDTO;
import com.J2EE.TourManagement.Model.Role;
import com.J2EE.TourManagement.Model.User;
import com.J2EE.TourManagement.Repository.RoleRepository;
import com.J2EE.TourManagement.Repository.UserRep;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class UserSer {

  private final PasswordEncoder passwordEncoder;
  private final UserRep userRep;
  private final RoleRepository roleRepository;
  public UserSer(UserRep userRep, RoleRepository roleRepository,
                 PasswordEncoder passwordEncoder) {
    this.userRep = userRep;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public User handleSaveUser(User user) {
    user.setRole(this.roleRepository.findByNameRole("user"));
    user.setStatus(true);
    return this.userRep.save(user);
  }

  public CreateUserDTO convertUserToResUserDto(User user) {
    CreateUserDTO resUserDTO = new CreateUserDTO();
    resUserDTO.setId(user.getId());
    resUserDTO.setFullName(user.getFullname());
    resUserDTO.setEmail(user.getEmail());
    resUserDTO.setCreatedAt(user.getCreatedAt());
    resUserDTO.setRefreshToken(user.getRefreshToken());
    resUserDTO.setStatus(user.getStatus());
    return resUserDTO;
  }

  public UserDTO convertUserToUserDto(User user) {
    UserDTO resUserDTO = new UserDTO();
    resUserDTO.setId(user.getId());
    resUserDTO.setFullName(user.getFullname());
    resUserDTO.setEmail(user.getEmail());
    resUserDTO.setCreatedAt(user.getCreatedAt());
    resUserDTO.setStatus(user.getStatus());
    resUserDTO.setUpdatedAt(user.getUpdatedAt());
    resUserDTO.setBookings(user.getBookings());
    resUserDTO.setRole(user.getRole());
    return resUserDTO;
  }

  public boolean isEmailExist(String email) {
    return this.userRep.existsByEmail(email);
  }

  public ResultPaginationDTO getAllUser(Specification<User> spec,
                                        Pageable pageable) {
    Page<User> pageUser = this.userRep.findAll(spec, pageable);
    ResultPaginationDTO result = new ResultPaginationDTO();
    Meta meta = new Meta();

    meta.setPageCurrent(pageable.getPageNumber() + 1);
    meta.setPageSize(pageable.getPageSize());

    meta.setPages(pageUser.getTotalPages());
    meta.setTotal(pageUser.getTotalElements());

    result.setMeta(meta);

    List<UserDTO> listUser =
        pageUser.getContent()
            .stream()
            .map(item
                 -> new UserDTO(item.getId(), item.getFullname(),
                                item.getEmail(), item.getStatus(),
                                item.getRole(), item.getCreatedAt(),
                                item.getUpdatedAt()))
            .collect(Collectors.toList());

    result.setResult(listUser);

    return result;
  }

  public boolean isIdExist(long id) { return this.userRep.existsById(id); }

  public User getUserById(long id) {
    Optional<User> userOp = this.userRep.findById(id);
    User user = new User();
    if (userOp.isPresent()) {
      user = userOp.get();
    }
    return user;
  }

  public User getUserByName(String name) {
    return this.userRep.findByEmail(name);
  }

  public User putUser(long id, User ReqUser) {
    User user = this.userRep.findById(id).isPresent()
                    ? this.userRep.findById(id).get()
                    : null;

    user.setFullname(ReqUser.getFullname());

    return this.userRep.save(user);
  }

  @Transactional
  public void UpdateRefreshToken(String token, String email) {
    User currentUser = this.getUserByName(email);
    if (currentUser != null) {
      currentUser.setRefreshToken(token);
      this.userRep.save(currentUser);
    }
  }

  public User getUserByRefreshTokenAnhEmail(String token, String email) {
    return this.userRep.findByRefreshTokenAndEmail(token, email);
  }

  public User setStatusUser(long id) {
    User user = this.userRep.findById(id).isPresent()
                    ? this.userRep.findById(id).get()
                    : null;

    user.setStatus(false);
    this.userRep.save(user);
    return user;
  }

  public User convertRegisterDtoToUser(RegisterDTO registerDTO) {
    User user = new User();
    user.setFullname(registerDTO.getFullname());
    user.setEmail(registerDTO.getEmail());
    user.setPassword(registerDTO.getPassword());
    user.setRole(this.roleRepository.findByNameRole("user"));
    return user;
  }

  public User createUserFromGoogle(OAuth2User oAuth2User) {
    String email = oAuth2User.getAttribute("email");
    String fullname = oAuth2User.getAttribute("name");

    User existingUser = this.userRep.findByEmail(email);
    if (existingUser != null)
      return existingUser;

    User newUser = new User();
    newUser.setEmail(email);
    newUser.setFullname(fullname);

    // set password ngẫu nhiên
    String randomPass = UUID.randomUUID().toString();
    newUser.setPassword(passwordEncoder.encode(randomPass));

    // gán role mặc định
    Role defaultRole = this.roleRepository.findByNameRole("user");
    newUser.setRole(defaultRole);
    newUser.setStatus(true);

    return this.userRep.save(newUser);
  }
}
