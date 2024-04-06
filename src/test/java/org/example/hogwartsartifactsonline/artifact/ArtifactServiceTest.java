package org.example.hogwartsartifactsonline.artifact;

import org.example.hogwartsartifactsonline.artifact.exception.ArtifcatNotFoundException;
import org.example.hogwartsartifactsonline.artifact.utils.IdWorker;
import org.example.hogwartsartifactsonline.wizard.Wizard;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtifactServiceTest {

    @Mock
    ArtifactRepository artifactRepository;

    @Mock
    IdWorker idWorker;
    @InjectMocks
    ArtifactService artifactService;

    List<Artifact> artifacts;

    @BeforeEach
    void setUp() {
        Artifact a1 = new Artifact();
        a1.setId("1");
        a1.setName("Deliminator");
        a1.setDescription("description of artifact");
        a1.setImageUrl("ImageUrl");

        Artifact a2 = new Artifact();
        a2.setId("2");
        a2.setName("Invisibility cloak");
        a2.setDescription("description of artifact");
        a2.setImageUrl("ImageUrl");
        this.artifacts = new ArrayList<>();
        this.artifacts.add(a1);
        this.artifacts.add(a2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindByIdSuccess() {
        // Given. Définir les données d'entrée et de sortie, le comportement du mock
        Artifact artifact = new Artifact();
        artifact.setId("1234");
        artifact.setName("Invisibility cloak");
        artifact.setDescription("description of artifact");
        artifact.setImageUrl("ImageUrl");

        Wizard wizard = new Wizard();
        wizard.setId(2);
        wizard.setName("Harry Potter");

        artifact.setOwner(wizard);

        when(artifactRepository.findById("1234")).thenReturn(Optional.of(artifact));


        // When. Appel de la méthode
        Artifact returnedArtifact = artifactService.findById("1234");

        //Then. Excepted
        assertThat(returnedArtifact.getId()).isEqualTo(artifact.getId());
        assertThat(returnedArtifact.getName()).isEqualTo(artifact.getName());
        assertThat(returnedArtifact.getDescription()).isEqualTo(artifact.getDescription());
        assertThat(returnedArtifact.getImageUrl()).isEqualTo(artifact.getImageUrl());

        verify(artifactRepository,times(1)).findById("1234");
    }
    @Test
    void testFindByIdNotFound() {
        // given
        when(artifactRepository.findById(Mockito.any(String.class))).thenReturn(Optional.empty());
        // When
        Throwable throwable = catchThrowable(() -> {
            Artifact returnedArtifact = artifactService.findById("1234");
        });
        // Excepted
        assertThat(throwable).isInstanceOf(ArtifcatNotFoundException.class).hasMessage("Could not find artifact with Id 1234 :(");
        verify(artifactRepository,times(1)).findById("1234");

    }
    @Test
    void testFindAllSuccess() {
        // Given
        when(artifactRepository.findAll()).thenReturn(this.artifacts);

        // When
        List<Artifact> actualArtifacts = this.artifactService.findAll();

        // Then
        assertThat(actualArtifacts.size()).isEqualTo(this.artifacts.size());
        verify(artifactRepository, times(1)).findAll();
    }

    @Test
    public void testSaveSuccess() {
        // Given
        Artifact newArtifact  = new Artifact();
        newArtifact.setName("Artifact S");
        newArtifact.setDescription("S for difficult level");
        newArtifact.setImageUrl("ImageUrl...");

        when(idWorker.nextId()).thenReturn(123456L);
        when(artifactRepository.save(newArtifact)).thenReturn(newArtifact);

        // When or Act
        Artifact savedArtifact = artifactService.save(newArtifact);

        // Then
        assertThat(savedArtifact.getId()).isEqualTo("123456");
        assertThat(savedArtifact.getName()).isEqualTo(newArtifact.getName());
        assertThat(savedArtifact.getDescription()).isEqualTo(newArtifact.getDescription());
        assertThat(savedArtifact.getImageUrl()).isEqualTo(newArtifact.getImageUrl());

        verify(artifactRepository, times(1)).save(newArtifact);
    }

    @Test
    public void testUpdateSuccess() {
        // Given
        Artifact oldArtifact = new Artifact();
        oldArtifact.setId("125899");
        oldArtifact.setName("Invisilibity");
        oldArtifact.setDescription("An invisibility is...");
        oldArtifact.setImageUrl("ImageUrl");

        Artifact update = new Artifact();
        update.setId("125899");
        update.setName("Invisilibity");
        update.setDescription("A new description");
        update.setImageUrl("ImageUrl");

        when(artifactRepository.findById("125899")).thenReturn(Optional.of(oldArtifact));
        when(artifactRepository.save(oldArtifact)).thenReturn(oldArtifact);

        // Act
        Artifact updatedArtifact = this.artifactService.update("125899", update);

        // Then
        assertThat(updatedArtifact.getId()).isEqualTo(update.getId());
        assertThat(updatedArtifact.getDescription()).isEqualTo(update.getDescription());

        verify(artifactRepository, times(1)).findById("125899");
        verify(artifactRepository, times(1)).save(oldArtifact);
    }
    @Test
    public void testUpdateNotFound() {
        // Given
        Artifact update = new Artifact();
        update.setName("Invisilibity");
        update.setDescription("A new description");
        update.setImageUrl("ImageUrl");

        when(artifactRepository.findById("125899")).thenReturn(Optional.empty());

        // Act
        assertThrows(ArtifcatNotFoundException.class, () -> {
            artifactService.update("125899", update);
        });

        // Then
        verify(artifactRepository, times(1)).findById("125899");
    }

    @Test
    public void testDeleteSuccess() {
        // Given
        Artifact artifact = new Artifact();
        artifact.setId("125899");
        artifact.setName("Invisilibity");
        artifact.setDescription("An invisibility is...");
        artifact.setImageUrl("ImageUrl");
        when(artifactRepository.findById("125899")).thenReturn(Optional.of(artifact));
        doNothing().when(artifactRepository).deleteById("125899");

        // Act
        artifactService.delete("125899");

        // Then
        verify(artifactRepository, times(1)).findById("125899");
        verify(artifactRepository, times(1)).deleteById("125899");
    }

    @Test
    public void testDeleteNotFound() {
        // Given
        when(artifactRepository.findById("125899")).thenReturn(Optional.empty());

        // Act
        assertThrows(ArtifcatNotFoundException.class, () -> {
            artifactService.delete("125899");
        });

        // Then
        verify(artifactRepository, times(1)).findById("125899");
    }

}