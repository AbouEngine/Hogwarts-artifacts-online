package org.example.hogwartsartifactsonline.artifact.dto;

import jakarta.validation.constraints.NotEmpty;
import org.example.hogwartsartifactsonline.wizard.dto.WizardDto;

public record ArtifactDto(String id,
                          @NotEmpty(message = "name is required") String name,
                          @NotEmpty(message = "descriotion is required") String description,
                          @NotEmpty(message = "imageUrl is required") String imageUrl,
                          WizardDto owner) {

}
