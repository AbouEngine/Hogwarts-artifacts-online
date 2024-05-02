package org.example.hogwartsartifactsonline.wizard;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hogwartsartifactsonline.system.StatusCode;
import org.example.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import org.example.hogwartsartifactsonline.wizard.dto.WizardDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class WizardControllerTest {

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    WizardServive wizardServive;

    List<Wizard> wizards;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.wizards = new ArrayList<>();
        Wizard w1 = new Wizard();
        w1.setId(1);
        w1.setName("Harry Potter");
        this.wizards.add(w1);

        Wizard w2 = new Wizard();
        w2.setId(2);
        w2.setName("Malefoy Drago");
        this.wizards.add(w2);

        Wizard w3 = new Wizard();
        w3.setId(3);
        w3.setName("Albus Dumbledore");
        this.wizards.add(w3);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindWizardByIdSuccess() throws Exception {
        // Given
        when(this.wizardServive.findById(1)).thenReturn(this.wizards.get(0));

        // Act and Then
        this.mockMvc.perform(get(this.baseUrl + "/wizards/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Harry Potter"));
    }

    @Test
    public void testFindWizardByIdNotFound() throws Exception {
        // Given
        when(wizardServive.findById(1)).thenThrow(new ObjectNotFoundException("wizard", 1));

        // When and Then. We need to invoke a fake http request
        this.mockMvc.perform(get(this.baseUrl + "/wizards/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 1 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    public void testFindAllWizardsSuccess() throws Exception {
        // Given
        when(this.wizardServive.findAll()).thenReturn(wizards);

        // Act and Then
        this.mockMvc.perform(get(this.baseUrl + "/wizards").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Harry Potter"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].name").value("Malefoy Drago"));
    }

    @Test
    public void testAddWizardSuccess() throws Exception {
        // Given
        WizardDto wizardDto = new WizardDto(null, "Malefoy Drago", 0);

        // Jackson Lib provides you to convert object to json
        String json = this.objectMapper.writeValueAsString(wizardDto);

        Wizard savedWizard = new Wizard();
        savedWizard.setId(1);
        savedWizard.setName("Malefoy Drago");
        when(this.wizardServive.save(Mockito.any(Wizard.class))).thenReturn(savedWizard);

        // Act and Then
        this.mockMvc.perform(post(this.baseUrl + "/wizards").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value(savedWizard.getName()));
    }

    @Test
    public void testUpdateWizardSuccess() throws Exception {
        // Given
        WizardDto wizardDto = new WizardDto(null, "Harry Potter", 0);
        // Jackson Lib provides you to convert object to json
        String json = this.objectMapper.writeValueAsString(wizardDto);

        Wizard updatedWizard  = new Wizard();
        updatedWizard.setId(1);
        updatedWizard.setName("Malefoy Drago");
        when(this.wizardServive.update(eq(1), Mockito.any(Wizard.class))).thenReturn(updatedWizard);

        // Act and Then
        this.mockMvc.perform(put(this.baseUrl + "/wizards/1").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value(updatedWizard.getName()));
    }

    @Test
    public void testUpdateWizardErrorWithNonExistentId() throws Exception {
        // Given
        WizardDto wizardDto = new WizardDto(null, "toto", 0);

        // Jackson Lib provides you to convert object to json
        String json = this.objectMapper.writeValueAsString(wizardDto);

        when(this.wizardServive.update(eq(4), Mockito.any(Wizard.class))).thenThrow(new ObjectNotFoundException("wizard", 4));

        // Act and Then
        this.mockMvc.perform(put(this.baseUrl + "/wizards/4").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 4 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    public void testDeleteWizardSuccess() throws Exception {
        // Given
        doNothing().when(this.wizardServive).delete(1);

        // Act and Then
        this.mockMvc.perform(delete(this.baseUrl + "/wizards/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    public void testDeleteWizardErrorWithNotExistentId() throws Exception {
        // Given
        doThrow(new ObjectNotFoundException("wizard", 1)).when(this.wizardServive).delete(1);

        // Act and Then
        this.mockMvc.perform(delete(this.baseUrl + "/wizards/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 1 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    public void testAssignArtifactSuccess() throws Exception {
        // Given
        doNothing().when(this.wizardServive).assignArtifact(2, "1");

        // Act and Then
        this.mockMvc.perform(put(this.baseUrl + "/wizards/2/artifacts/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Artifact Assignment Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    public void testAssignArtifactErrorWithNonExistentWizradId() throws Exception {
        // Given
        doThrow(new ObjectNotFoundException("wizard", 5)).when(this.wizardServive).assignArtifact(5, "1");

        // Act and Then
        this.mockMvc.perform(put(this.baseUrl + "/wizards/5/artifacts/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 5 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    public void testAssignArtifactErrorWithNonExistentArtifactId() throws Exception {
        // Given
        doThrow(new ObjectNotFoundException("artifact", "4")).when(this.wizardServive).assignArtifact(1, "4");

        // Act and Then
        this.mockMvc.perform(put(this.baseUrl + "/wizards/1/artifacts/4").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 4 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

}