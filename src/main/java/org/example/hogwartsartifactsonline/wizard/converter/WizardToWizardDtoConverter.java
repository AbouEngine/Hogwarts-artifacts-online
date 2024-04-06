package org.example.hogwartsartifactsonline.wizard.converter;

import org.example.hogwartsartifactsonline.wizard.Wizard;
import org.example.hogwartsartifactsonline.wizard.dto.WizardDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class WizardToWizardDtoConverter implements Converter<Wizard, WizardDto> {
    @Override
    public WizardDto convert(Wizard source) {
        WizardDto wizardDto = new WizardDto(source.getId(), source.getName(), source.getNumberOfArtifact());
        return wizardDto;
    }
}
