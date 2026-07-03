package com.foodtruck.envios_service.exception;

import lombok.*; import java.time.LocalDateTime;
@Data @AllArgsConstructor
public class ApiErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}