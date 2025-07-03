package com.nhanab.demosecurity.dto;

import lombok.Data;

public record ErrorResponse(int status, String message, String error) {
}
