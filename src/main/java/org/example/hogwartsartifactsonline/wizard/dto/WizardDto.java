package org.example.hogwartsartifactsonline.wizard.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Null;

public record WizardDto(@Null(message = "id must be null") Integer id,
                        @NotEmpty(message = "name is required") String name,
                        Integer numberOfArtifact) {
}
