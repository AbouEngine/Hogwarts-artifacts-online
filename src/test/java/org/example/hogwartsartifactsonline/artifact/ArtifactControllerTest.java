package org.example.hogwartsartifactsonline.artifact;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hogwartsartifactsonline.artifact.dto.ArtifactDto;
import org.example.hogwartsartifactsonline.artifact.exception.ArtifcatNotFoundException;
import org.example.hogwartsartifactsonline.system.StatusCode;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class ArtifactControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    ArtifactService artifactService;

    @Autowired
    ObjectMapper objectMapper;

    List<Artifact> artifacts;

    @BeforeEach
    void setUp() {
        this.artifacts = new ArrayList<>();
        Artifact a1 = new Artifact();
        a1.setId("1");
        a1.setName("Deliminator");
        a1.setDescription("Deliminator description");
        a1.setImageUrl("ImageUrl");
        this.artifacts.add(a1);

        Artifact a2 = new Artifact();
        a2.setId("2");
        a2.setName("Invisibility");
        a2.setDescription("Invisibility description");
        a2.setImageUrl("ImageUrl");
        this.artifacts.add(a2);

        Artifact a3 = new Artifact();
        a3.setId("3");
        a3.setName("Shadow");
        a3.setDescription("Deliminator description");
        a3.setImageUrl("ImageUrl");
        this.artifacts.add(a3);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindArtifactById() throws Exception {
        // Given
        when(artifactService.findById("1")).thenReturn(this.artifacts.get(0));

        // When and Then. We need to invoke a fake http request
        this.mockMvc.perform(get("/api/v1/artifacts/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.name").value("Deliminator"));
    }

    @Test
    void testFindArtifactByIdNotFound() throws Exception {
        // Given
        when(artifactService.findById("1")).thenThrow(new ArtifcatNotFoundException("1"));

        // When and Then. We need to invoke a fake http request
        this.mockMvc.perform(get("/api/v1/artifacts/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testFindAllArtifactsSuccess() throws Exception {
        // Given
        when(artifactService.findAll()).thenReturn(this.artifacts);

        // When and Then
        this.mockMvc.perform(get("/api/v1/artifacts").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data").value(Matchers.hasSize(this.artifacts.size())))
                .andExpect(jsonPath("$.data[0].id").value("1"))
                .andExpect(jsonPath("$.data[0].name").value("Deliminator"))
                .andExpect(jsonPath("$.data[1].id").value("2"))
                .andExpect(jsonPath("$.data[1].name").value("Invisibility"));
    }

    @Test
    public void testAddArtifactSuccess() throws Exception {
        // Given
        ArtifactDto artifactDto = new ArtifactDto(null, "toto", "desc", "imageUrl", null );

        // Jackson Lib provides you to convert object to json
        String json = this.objectMapper.writeValueAsString(artifactDto);

        Artifact savedArtifact  = new Artifact();
        savedArtifact.setId("1234567");
        savedArtifact.setName("toto");
        savedArtifact.setDescription("desc");
        savedArtifact.setImageUrl("imageUrl");
        when(this.artifactService.save(Mockito.any(Artifact.class))).thenReturn(savedArtifact);

        // Act and Then
        this.mockMvc.perform(post("/api/v1/artifacts").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value(savedArtifact.getName()))
                .andExpect(jsonPath("$.data.description").value(savedArtifact.getDescription()))
                .andExpect(jsonPath("$.data.imageUrl").value(savedArtifact.getImageUrl()));

    }
    @Test
    public void testUpdateArtifactSuccess() throws Exception {
        // Given
        ArtifactDto artifactDto = new ArtifactDto("1234567", "Invisilibity", "A new description", "ImageUrl", null );

        // Jackson Lib provides you to convert object to json
        String json = this.objectMapper.writeValueAsString(artifactDto);

        Artifact updatedArtifact  = new Artifact();
        updatedArtifact.setId("1234567");
        updatedArtifact.setName("Invisilibity");
        updatedArtifact.setDescription("A new description");
        updatedArtifact.setImageUrl("ImageUrl");
        when(this.artifactService.update(eq("1234567"), Mockito.any(Artifact.class))).thenReturn(updatedArtifact);

        // Act and Then
        this.mockMvc.perform(put("/api/v1/artifacts/1234567").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value("1234567"))
                .andExpect(jsonPath("$.data.name").value(updatedArtifact.getName()))
                .andExpect(jsonPath("$.data.description").value(updatedArtifact.getDescription()))
                .andExpect(jsonPath("$.data.imageUrl").value(updatedArtifact.getImageUrl()));
    }

    @Test
    public void testUpdateArtifactErrorWithNonExistentId() throws Exception {
        // Given
        ArtifactDto artifactDto = new ArtifactDto("1234567", "Invisilibity", "A new description", "ImageUrl", null );

        // Jackson Lib provides you to convert object to json
        String json = this.objectMapper.writeValueAsString(artifactDto);

        when(this.artifactService.update(eq("1234567"), Mockito.any(Artifact.class))).thenThrow(new ArtifcatNotFoundException("1234567"));

        // Act and Then
        this.mockMvc.perform(put("/api/v1/artifacts/1234567").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1234567 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }
    @Test
    public void testDeleteArtifactSuccess() throws Exception {
        // Given
        doNothing().when(this.artifactService).delete("1234567");

        // When and Then
        this.mockMvc.perform(delete("/api/v1/artifacts/1234567").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    public void testDeleteArtifactErrorWithExistentId() throws Exception {
        // Given
        doThrow(new ArtifcatNotFoundException("1234567")).when(this.artifactService).delete("1234567");

        // When and Then
        this.mockMvc.perform(delete("/api/v1/artifacts/1234567").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1234567 :("))
                .andExpect(jsonPath("$.data").isEmpty());

    }
}