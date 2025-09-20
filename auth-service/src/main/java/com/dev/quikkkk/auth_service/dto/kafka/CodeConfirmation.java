package com.dev.quikkkk.auth_service.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CodeConfirmation {
    private String code;
    private String email;
    private String userId;
    private String templateType;
    private Map<String, Object> templateVariables;

    public CodeConfirmation(String code, String email, String userId) {
        this.code = code;
        this.email = email;
        this.userId = userId;
        this.templateType = "EMAIL_VERIFICATION";
    }
}
