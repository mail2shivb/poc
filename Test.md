package com.ubs.ib.gb.companyprofile.unittest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubs.ib.gb.companyprofile.controller.TemplateController;
import com.ubs.ib.gb.companyprofile.dto.TemplateDTO;
import com.ubs.ib.gb.companyprofile.enums.TemplateGroup;
import com.ubs.ib.gb.companyprofile.enums.Type;
import com.ubs.ib.gb.companyprofile.service.TemplateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.v2.MockBean; // ✅ new import
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

    @MockBean // ✅ uses new v2 namespace
    private TemplateServiceImpl templateServiceImpl;

    private TemplateDTO dto;

    @BeforeEach
    void setup() {
        dto = TemplateDTO.builder()
                .id("template-1")
                .type(Type.COMPANY_PROFILE.getCode())
                .group(TemplateGroup.USER_DEFINED.getValue())
                .createdBy("9874563")
                .description("JUnit DTO")
                .build();
    }

    @Test
    void shouldReturnTemplates() throws Exception {
        Mockito.when(templateServiceImpl.getTemplatesByUserIdAndTypeAndGroup(
                Type.COMPANY_PROFILE.getCode(), TemplateGroup.USER_DEFINED.getValue()))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/template")
                        .param("type", "company-profile")
                        .param("group", "user-defined")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value("template-1"));
    }
}
