package com.example.demo.dto.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetail {
    private Timestamp timestamp;
    private int code;
    private String detail;
}
