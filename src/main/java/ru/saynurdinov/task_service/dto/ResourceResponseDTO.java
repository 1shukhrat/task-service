package ru.saynurdinov.task_service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ResourceResponseDTO<T> extends ResponseDTO{
    private T body;

    public ResourceResponseDTO(String message, T body) {
        super(message);
        this.body = body;
    }
}
