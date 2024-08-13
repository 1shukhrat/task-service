package ru.saynurdinov.task_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ResponseDTO {

    private LocalDateTime date;
    private String message;

    public ResponseDTO(String message) {
        this.message = message;
        date = LocalDateTime.now();
    }

}
