package org.example.hogwartsartifactsonline.wizard;

import jakarta.validation.Valid;
import org.example.hogwartsartifactsonline.system.Result;
import org.example.hogwartsartifactsonline.system.StatusCode;
import org.example.hogwartsartifactsonline.wizard.converter.WizardDtoToWizardConverter;
import org.example.hogwartsartifactsonline.wizard.converter.WizardToWizardDtoConverter;
import org.example.hogwartsartifactsonline.wizard.dto.WizardDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/wizards")
public class WizardController {

    private final WizardServive wizardServive;
    private final WizardToWizardDtoConverter wizardToWizardDtoConverter;

    private final WizardDtoToWizardConverter wizardDtoToWizardConverter;

    public WizardController(WizardServive wizardServive,
                            WizardToWizardDtoConverter wizardToWizardDtoConverter,
                            WizardDtoToWizardConverter wizardDtoToWizardConverter) {
        this.wizardServive = wizardServive;
        this.wizardToWizardDtoConverter = wizardToWizardDtoConverter;
        this.wizardDtoToWizardConverter = wizardDtoToWizardConverter;
    }

    @GetMapping("/{wizardId}")
    public Result findWizardById(@PathVariable Integer wizardId) {
        Wizard wizardFound = this.wizardServive.findById(wizardId);
        WizardDto wizadFoundDto = this.wizardToWizardDtoConverter.convert(wizardFound);
        return new Result(true, StatusCode.SUCCESS, "Find One Success", wizadFoundDto);
    }

    @GetMapping
    public Result findAllWizards() {
        List<Wizard> wizardListFound = this.wizardServive.findAll();
        List<WizardDto> wizardDtoList = wizardListFound.stream()
                .map(this.wizardToWizardDtoConverter::convert)
                .collect(Collectors.toList());
        return new Result(true, StatusCode.SUCCESS, "Find All Success", wizardDtoList);
    }

    @PostMapping
    public Result addWizard(@Valid @RequestBody WizardDto wizardDto) {
        Wizard wizard = this.wizardDtoToWizardConverter.convert(wizardDto);
        Wizard savedWizard = this.wizardServive.save(wizard);
        WizardDto savedWizardDto = this.wizardToWizardDtoConverter.convert(savedWizard);
        return new Result(true, StatusCode.SUCCESS, "Add Success", savedWizardDto);
    }

    @PutMapping("/{wizardId}")
    public Result updateWizard(@PathVariable Integer wizardId, @Valid @RequestBody WizardDto updatedWizardDto) {
        Wizard update = this.wizardDtoToWizardConverter.convert(updatedWizardDto);
        Wizard updatedWizard = this.wizardServive.update(wizardId, update);
        WizardDto updateWizardDto = this.wizardToWizardDtoConverter.convert(updatedWizard);
        return new Result(true, StatusCode.SUCCESS, "Update Success", updateWizardDto);
    }

    @DeleteMapping("/{wizardId}")
    public Result deleteWizard(@PathVariable Integer wizardId) {
        this.wizardServive.delete(wizardId);
        return new Result(true, StatusCode.SUCCESS, "Delete Success");
    }
}
