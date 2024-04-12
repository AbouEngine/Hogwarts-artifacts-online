package org.example.hogwartsartifactsonline.wizard;

import jakarta.transaction.Transactional;
import org.example.hogwartsartifactsonline.artifact.Artifact;
import org.example.hogwartsartifactsonline.artifact.ArtifactRepository;
import org.example.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class WizardServive {
    private final WizardRepository wizardRepository;

    private final ArtifactRepository artifactRepository;

    public WizardServive(WizardRepository wizardRepository, ArtifactRepository artifactRepository) {
        this.wizardRepository = wizardRepository;
        this.artifactRepository = artifactRepository;
    }

    public Wizard findById(Integer wizardId) {
        return this.wizardRepository.findById(wizardId)
                .orElseThrow(() -> new ObjectNotFoundException("wizard", wizardId));
    }

    public List<Wizard> findAll() {
        return this.wizardRepository.findAll();
    }

    public Wizard save(Wizard wizard) {
        return this.wizardRepository.save(wizard);
    }

    public Wizard update(Integer wizardId, Wizard update) {
        return this.wizardRepository.findById(wizardId)
                .map(oldWizard -> {
                    oldWizard.setName(update.getName());
                    return this.wizardRepository.save(oldWizard);
                })
                .orElseThrow(() -> new ObjectNotFoundException("wizard", wizardId));
    }

    public void delete(Integer wizardId) {
        Wizard deletedWizard = this.wizardRepository.findById(wizardId)
                .orElseThrow(() -> new ObjectNotFoundException("wizard", wizardId));
        // Avant de supprimer le wizard on met null ses artifacts
        deletedWizard.removeAllArtifacts();
        this.wizardRepository.deleteById(wizardId);
    }

    public void assignArtifact(Integer wizardId, String artifactId) {
        // Find artifact
        Artifact artifactToBeAssigned = this.artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ObjectNotFoundException("artifact", artifactId));
        // Find wizrad
        Wizard wizard = this.wizardRepository.findById(wizardId)
                .orElseThrow(() -> new ObjectNotFoundException("wizard", wizardId));

        // assign artifact
        if (artifactToBeAssigned.getOwner() != null) {
            artifactToBeAssigned.getOwner().removeArtifact(artifactToBeAssigned);
        }
        wizard.addArtifact(artifactToBeAssigned);

    }
}
