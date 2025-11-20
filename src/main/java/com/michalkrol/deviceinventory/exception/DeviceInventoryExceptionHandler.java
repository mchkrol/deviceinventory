package com.michalkrol.deviceinventory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DeviceInventoryExceptionHandler {

    @ExceptionHandler(DeviceInventoryException.class)
    public ResponseEntity<String> handleValidationException(DeviceInventoryException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }
}
