package org.example.hogwartsartifactsonline.wizard;

import org.example.hogwartsartifactsonline.artifact.Artifact;
import org.example.hogwartsartifactsonline.artifact.ArtifactRepository;
import org.example.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WizardServiveTest {

    @Mock
    WizardRepository wizardRepository;

    @Mock
    ArtifactRepository artifactRepository;

    @InjectMocks
    WizardServive wizardServive;

    List<Wizard> wizards;

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
    void testFindByIdSuccess() {
        // Given
        Wizard wizard = new Wizard();
        wizard.setId(1);
        wizard.setName("Harry Potter");
        when(this.wizardRepository.findById(1)).thenReturn(Optional.of(wizard));

        // Act
        Wizard wizardFound = this.wizardServive.findById(1);

        // Then
        assertThat(wizardFound.getId()).isEqualTo(wizard.getId());
        assertThat(wizardFound.getName()).isEqualTo(wizard.getName());

        verify(this.wizardRepository, times(1)).findById(1);
    }

    @Test
    public void testFindByIdNotFound() {
        // Given
        when(this.wizardRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.empty());

        // Act
        Throwable throwable = catchThrowable(() -> {
            this.wizardServive.findById(2);
        });

        //Then
        assertThat(throwable).isInstanceOf(ObjectNotFoundException.class).hasMessage("Could not find wizard with Id 2 :(");
        verify(this.wizardRepository, times(1)).findById(2);
    }

    @Test
    public void testFindAllSuccess() {
        // Given
        when(this.wizardRepository.findAll()).thenReturn(wizards);

        // Act
        List<Wizard> retournedWizards = this.wizardServive.findAll();

        // Then
        assertThat(retournedWizards.size()).isEqualTo(this.wizards.size());
        verify(this.wizardRepository, times(1)).findAll();

    }

    @Test
    public void testSaveSuccess() {
        // Given
        Wizard newWizard = new Wizard();
        newWizard.setId(1);
        newWizard.setName("Neville Rail");
        when(this.wizardRepository.save(newWizard)).thenReturn(newWizard);

        // Act
        Wizard savedWizard = this.wizardServive.save(newWizard);

        // Then
        assertThat(savedWizard.getId()).isEqualTo(newWizard.getId());
        assertThat(savedWizard.getName()).isEqualTo(newWizard.getName());
        verify(this.wizardRepository, times(1)).save(newWizard);
    }

    @Test
    public void testUpdateSuccess() {
        // Given
        Wizard oldWizard = new Wizard();
        oldWizard.setId(1);
        oldWizard.setName("Harry potter");

        Wizard update = new Wizard();
        update.setName("Malefoy Drago");

        when(this.wizardRepository.findById(1)).thenReturn(Optional.of(oldWizard));
        when(this.wizardRepository.save(oldWizard)).thenReturn(oldWizard);

        // Act
        Wizard updatedWizard = this.wizardServive.update(1, update);

        // Then
        assertThat(updatedWizard.getId()).isEqualTo(1);
        assertThat(updatedWizard.getName()).isEqualTo(update.getName());

        verify(this.wizardRepository, times(1)).findById(1);
        verify(this.wizardRepository, times(1)).save(oldWizard);
    }

    @Test
    public void testUpdaateNotFound() {
        // Given
        Wizard update = new Wizard();
        update.setId(1);
        update.setName("Harry potter");
        when(this.wizardRepository.findById(1)).thenReturn(Optional.empty());

        // Act
        assertThrows(ObjectNotFoundException.class, () -> {
            this.wizardServive.update(1, update);
        });

        // Then
        verify(this.wizardRepository, times(1)).findById(1);
    }

    @Test
    public void testDeleteSuccess() {
        // Given
        Wizard wizard = new Wizard();
        wizard.setId(1);
        wizard.setName("Harry Potter");

        when(this.wizardRepository.findById(1)).thenReturn(Optional.of(wizard));
        doNothing().when(this.wizardRepository).deleteById(1);

        // Act
        this.wizardServive.delete(1);

        // Then
        verify(this.wizardRepository, times(1)).findById(1);
        verify(this.wizardRepository, times(1)).deleteById(1);
    }

    @Test
    public void testDeleteNotFound() {
        // Given
        when(this.wizardRepository.findById(3)).thenReturn(Optional.empty());

        // Act
        assertThrows(ObjectNotFoundException.class, () -> {
            this.wizardServive.delete(3);
        });

        // Then
        verify(this.wizardRepository, times(1)).findById(3);
    }

    @Test
    public void testAssignArtifactSuccess() {
        // Given
        Artifact artifact = new Artifact();
        artifact.setId("1");
        artifact.setName("Invisibility Cloak");
        artifact.setDescription("Description of...");
        artifact.setImageUrl("ImageUrl");

        Wizard w1 = new Wizard();
        w1.setId(1);
        w1.setName("Harry Potter");
        w1.addArtifact(artifact);

        Wizard w2 = new Wizard();
        w2.setId(2);
        w2.setName("Malefoy Drago");
        when(this.artifactRepository.findById("1")).thenReturn(Optional.of(artifact));
        when(this.wizardRepository.findById(2)).thenReturn(Optional.of(w2));


        // Act
        this.wizardServive.assignArtifact(2, "1");


        // Then
        assertThat(artifact.getOwner().getId()).isEqualTo(2);
        assertThat(w2.getArtifacts()).contains(artifact);
    }

    @Test
    public void testAssignArtifactErrorWithNonExistentWizardId() {
        // Given
        Artifact artifact = new Artifact();
        artifact.setId("1");
        artifact.setName("Invisibility Cloak");
        artifact.setDescription("Description of...");
        artifact.setImageUrl("ImageUrl");

        Wizard w1 = new Wizard();
        w1.setId(1);
        w1.setName("Harry Potter");
        w1.addArtifact(artifact);

        when(this.artifactRepository.findById("1")).thenReturn(Optional.of(artifact));
        when(this.wizardRepository.findById(2)).thenReturn(Optional.empty());


        // Act
        Throwable throwable = assertThrows(ObjectNotFoundException.class, () -> {
            this.wizardServive.assignArtifact(2, "1");
        });

        // Then
        assertThat(throwable).isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find wizard with Id 2 :(");
        assertThat(artifact.getOwner().getId()).isEqualTo(1);
    }

    @Test
    public void testAssignArtifactErrorWithNonExistentArtifactId() {
        // Given

        when(this.artifactRepository.findById("1")).thenReturn(Optional.empty());


        // Act
        Throwable throwable = assertThrows(ObjectNotFoundException.class, () -> {
            this.wizardServive.assignArtifact(2, "1");
        });

        // Then
        assertThat(throwable).isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find artifact with Id 1 :(");
    }
}