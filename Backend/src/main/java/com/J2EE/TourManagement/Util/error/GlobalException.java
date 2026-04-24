package com.J2EE.TourManagement.Util.error;

import com.J2EE.TourManagement.Model.RestResponse;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalException {

    // ❗LỖI ĐĂNG NHẬP
    @ExceptionHandler({
        UsernameNotFoundException.class,
        BadCredentialsException.class
    })
    public ResponseEntity<RestResponse<Object>> handleLoginException(Exception exception) {
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(exception.getMessage());
        res.setMessage("Thông tin đăng nhập không hợp lệ.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    // ❗VALIDATION @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Object>> validationError(MethodArgumentNotValidException ex) {

        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError("Dữ liệu không hợp lệ");

        List<String> errors = fieldErrors.stream()
            .map(FieldError::getDefaultMessage)
            .toList();

        res.setMessage(errors.size() > 1 ? errors : errors.get(0));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    // ❗404 – Không tìm thấy API
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<RestResponse<Object>> handleNoResourceFound(NoResourceFoundException ex) {
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setError("Resource Not Found");
        res.setMessage("Không tìm thấy đường dẫn: " + ex.getResourcePath());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }

    // ❗INVALID (lỗi nghiệp vụ của bạn)
    @ExceptionHandler(InvalidException.class)
    public ResponseEntity<RestResponse<Object>> handleIdInvalidException(InvalidException ex) {
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError("Conflict");
        res.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    // ❗UPLOAD FILE ERROR
    @ExceptionHandler(StorageException.class)
    public ResponseEntity<RestResponse<Object>> handleStorageException(StorageException ex) {
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError("Exception upload file...");
        res.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    // ❗TẤT CẢ LỖI KHÁC
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
      RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        res.setError("Lỗi hệ thống");
        res.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }
}
