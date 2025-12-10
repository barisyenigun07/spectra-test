package com.spectra.control.mapper;

import com.spectra.commons.dto.locator.LocatorDTO;
import com.spectra.commons.dto.step.StepDTO;
import com.spectra.control.model.Step;
import org.springframework.stereotype.Component;

@Component
public class StepMapper {
    public StepDTO toDto(Step entity) {
        return new StepDTO(
                entity.getId(),
                entity.getOrderIndex(),
                entity.getAction(),
                new LocatorDTO(entity.getLocator().getType(), entity.getLocator().getValue()),
                entity.getParams()
        );
    }
}
