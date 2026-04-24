package com.J2EE.TourManagement.Controller;

import com.J2EE.TourManagement.Model.DTO.CreateUserDTO;
import com.J2EE.TourManagement.Model.DTO.UserDTO;
import com.J2EE.TourManagement.Model.User;
import com.J2EE.TourManagement.Service.UserSer;
import com.J2EE.TourManagement.Util.annotation.ApiMessage;
import com.J2EE.TourManagement.Util.error.InvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import java.io.InvalidClassException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserController {
  private final UserSer userSer;
  private final PasswordEncoder passwordEncoder;
  public UserController(UserSer userSer, PasswordEncoder passwordEncoder) {
    this.userSer = userSer;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/users/create")
  @ApiMessage("Thêm người dùng thành công.")
  public ResponseEntity<?> postNewUser(@RequestBody @Valid User newUser)
      throws InvalidException {
    boolean isEmailExist = this.userSer.isEmailExist(newUser.getEmail());
    if (isEmailExist) {
      throw new InvalidException("Email đã tồn tại. Vui lòng nhập email khác.");
    }

    String hashPassword = this.passwordEncoder.encode(newUser.getPassword());
    newUser.setPassword(hashPassword);
    User user = this.userSer.handleSaveUser(newUser);
    CreateUserDTO resUserDTO = this.userSer.convertUserToResUserDto(user);
    return ResponseEntity.status(HttpStatus.CREATED).body(resUserDTO);
  }

  @GetMapping("/users")
  public ResponseEntity<?> fetchAllUser(@Filter Specification<User> spec,
                                        Pageable pageable) {
    return ResponseEntity.ok().body(this.userSer.getAllUser(spec, pageable));
  }

  @GetMapping("/users/{id}")
  public ResponseEntity<UserDTO> getById(@PathVariable("id") long id)
      throws InvalidException {
    boolean isId = this.userSer.isIdExist(id);
    if (!isId) {
      throw new InvalidException("Id không tồn tại.");
    }
    User user = this.userSer.getUserById(id);
    return ResponseEntity.ok(this.userSer.convertUserToUserDto(user));
  }

  @PutMapping("/users/{id}")
  @ApiMessage("cập nhật người dùng thành công")
  public ResponseEntity<UserDTO> UpdateUser(@PathVariable("id") long id,
                                            @RequestBody User userCurrent)
      throws InvalidException {

    boolean isId = this.userSer.isIdExist(id);
    if (!isId) {
      throw new InvalidException("Id không tồn tại.");
    }
    User user = this.userSer.putUser(id, userCurrent);
    return ResponseEntity.ok(this.userSer.convertUserToUserDto(user));
  }

  @PutMapping("users/delete/{id}")
  public ResponseEntity<UserDTO> putMethodName(@PathVariable("id") long id) throws InvalidException {
   boolean isId = this.userSer.isIdExist(id);
    if (!isId) {
      throw new InvalidException("Id không tồn tại.");
    }

    User user = this.userSer.setStatusUser(id);
    return ResponseEntity.ok(this.userSer.convertUserToUserDto(user));
  }
  
}
