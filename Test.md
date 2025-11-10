Perfect — now that I can see both your TemplateControllerTest structure and your enum setup (Type and TemplateGroup), here’s how to correctly write MockMvc-based test cases for your controller method:

⸻

✅ CONTROLLER UNDER TEST

From your earlier screenshot:

@GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<List<TemplateDTO>> getTemplatesByUserAndTypeAndGroup(
        @RequestParam String type,
        @RequestParam String group) {
    log.info("Received request to Get Templates by type: '{}' and group: '{}'", type, group);
    List<TemplateDTO> templatesByUserIdAndType = templateServiceImpl
            .getTemplatesByUserIdAndTypeAndGroup(type, group);
    log.info("Successfully fetched {} templates for type: '{}' and group: '{}'",
            templatesByUserIdAndType.size(), type, group);
    return ResponseEntity.ok(templatesByUserIdAndType);
}


⸻

✅ TEST CLASS IMPLEMENTATION (TemplateControllerTest.java)

This test uses @WebMvcTest, mocks your service, and verifies the JSON and HTTP status.

package com.ubs.ib.gb.companyprofile.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubs.ib.gb.companyprofile.dto.TemplateDTO;
import com.ubs.ib.gb.companyprofile.enums.TemplateGroup;
import com.ubs.ib.gb.companyprofile.enums.Type;
import com.ubs.ib.gb.companyprofile.service.TemplateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TemplateController.class)
@AutoConfigureMockMvc(addFilters = false)
class TemplateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TemplateServiceImpl templateServiceImpl;

    private TemplateDTO dto1;
    private TemplateDTO dto2;

    @BeforeEach
    void setup() {
        dto1 = TemplateDTO.builder()
                .id("template-12345")
                .type(Type.COMPANY_PROFILE.getCode())
                .createdBy("9874563")
                .name("Template A")
                .group(TemplateGroup.USER_DEFINED.getValue())
                .description("Test Template A")
                .build();

        dto2 = TemplateDTO.builder()
                .id("template-67890")
                .type(Type.SR_MEET_PREP.getCode())
                .createdBy("9874563")
                .name("Template B")
                .group(TemplateGroup.LIBRARY.getValue())
                .description("Test Template B")
                .build();
    }

    @Test
    @DisplayName("✅ Should return 200 and 2 templates when valid type and group provided")
    void shouldReturnTemplates_whenTypeAndGroupAreValid() throws Exception {
        Mockito.when(templateServiceImpl.getTemplatesByUserIdAndTypeAndGroup(
                        Type.COMPANY_PROFILE.getCode(),
                        TemplateGroup.USER_DEFINED.getValue()))
                .thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/template")
                        .param("type", Type.COMPANY_PROFILE.getCode())
                        .param("group", TemplateGroup.USER_DEFINED.getValue())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value("template-12345"))
                .andExpect(jsonPath("$[0].type").value("company-profile"))
                .andExpect(jsonPath("$[0].group").value("user-defined"))
                .andExpect(jsonPath("$[1].id").value("template-67890"))
                .andExpect(jsonPath("$[1].group").value("library"));

        Mockito.verify(templateServiceImpl).getTemplatesByUserIdAndTypeAndGroup(
                eq(Type.COMPANY_PROFILE.getCode()),
                eq(TemplateGroup.USER_DEFINED.getValue())
        );
    }

    @Test
    @DisplayName("✅ Should return 400 when type parameter is missing")
    void shouldReturnBadRequest_whenTypeMissing() throws Exception {
        mockMvc.perform(get("/api/template")
                        .param("group", TemplateGroup.LIBRARY.getValue())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("✅ Should return empty list (200 OK) when no templates found")
    void shouldReturnEmptyList_whenNoTemplatesFound() throws Exception {
        Mockito.when(templateServiceImpl.getTemplatesByUserIdAndTypeAndGroup(
                Type.COMPANY_PROFILE.getCode(), TemplateGroup.USER_DEFINED.getValue()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/template")
                        .param("type", Type.COMPANY_PROFILE.getCode())
                        .param("group", TemplateGroup.USER_DEFINED.getValue())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("✅ Should return 500 when service throws exception")
    void shouldReturn500_whenServiceThrowsException() throws Exception {
        Mockito.when(templateServiceImpl.getTemplatesByUserIdAndTypeAndGroup("company-profile", "user-defined"))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/template")
                        .param("type", "company-profile")
                        .param("group", "user-defined")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}


⸻

🧩 Key Points

Aspect	Description
Enum Integration	Uses Type.COMPANY_PROFILE.getCode() and TemplateGroup.USER_DEFINED.getValue() to match what controller expects (String).
Mock Setup	@WebMvcTest auto-loads controller, mocks out the service.
Assertions	Confirms HTTP 200 + JSON structure using jsonPath.
Error Paths	Tests for missing parameters and runtime exceptions.
Compatibility	Works fine with your existing setup using TemplateServiceImpl instead of an interface.


⸻

Would you like me to extend this to also cover your POST /api/template test (testCreateTemplate_ShouldReturn201) that you started in the screenshot? I can complete that next with request body + mock service verification.
