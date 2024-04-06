package org.example.hogwartsartifactsonline.artifact.converter;

import org.example.hogwartsartifactsonline.artifact.Artifact;
import org.example.hogwartsartifactsonline.artifact.dto.ArtifactDto;
import org.example.hogwartsartifactsonline.wizard.converter.WizardToWizardDtoConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ArtifactToArtifactDtoConverter implements Converter<Artifact, ArtifactDto> {
    private final WizardToWizardDtoConverter wizardToWizardDtoConverter;

    public ArtifactToArtifactDtoConverter(WizardToWizardDtoConverter wizardToWizardDtoConverter) {
        this.wizardToWizardDtoConverter = wizardToWizardDtoConverter;
    }

    @Override
    public ArtifactDto convert(Artifact source) {
        ArtifactDto artifactDto = new ArtifactDto(source.getId(),
                                                    source.getName(),
                                                    source.getDescription(),
                                                    source.getImageUrl(),
                                                    source.getOwner() != null ? this.wizardToWizardDtoConverter.convert(source.getOwner()) : null);
        return artifactDto;
    }
}
