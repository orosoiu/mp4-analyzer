package ro.occam.mp4analyzer.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ValidationError {
    private String field;
    private String error;
}
