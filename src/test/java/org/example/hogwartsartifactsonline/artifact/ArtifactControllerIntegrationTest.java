package org.example.hogwartsartifactsonline.artifact;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hogwartsartifactsonline.system.StatusCode;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests for Artifact API endpoints")
@Tag("integration")
public class ArtifactControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Value("${api.endpoint.base-url}")
    String baseUrl;
    @Autowired
    ObjectMapper objectMapper;

    String token;

    @BeforeEach
    void setUp() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(post(this.baseUrl + "/users/login")
                .with(httpBasic("Eren", "123456")));
        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(content);
        this.token = "Bearer " + jsonObject.getJSONObject("data").getString("token");
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD) // reset la BD avant de lancer ce test
    void testFindAllArtifactsSuccess() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + "/artifacts").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(6)));
    }

    @Test
    void testAddArtifactSuccess() throws Exception {
        Artifact a = new Artifact();
        a.setName("new artifact");
        a.setDescription("desc");
        a.setImageUrl("img");
        String json = this.objectMapper.writeValueAsString(a);
        this.mockMvc.perform(post(this.baseUrl + "/artifacts").contentType(MediaType.APPLICATION_JSON).header("Authorization", this.token).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value("new artifact"))
                .andExpect(jsonPath("$.data.description").value("desc"))
                .andExpect(jsonPath("$.data.imageUrl").value("img"));

        this.mockMvc.perform(get(this.baseUrl + "/artifacts").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(7)));
    }
}
