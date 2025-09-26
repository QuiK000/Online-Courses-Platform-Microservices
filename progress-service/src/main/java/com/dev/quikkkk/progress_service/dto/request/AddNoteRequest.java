package com.dev.quikkkk.progress_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class AddNoteRequest {
    @NotBlank(message = "VALIDATION.NOTE.NOTES.NOT_BLANK")
    private String notes;
}
