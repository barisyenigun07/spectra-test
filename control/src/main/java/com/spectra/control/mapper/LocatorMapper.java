package com.spectra.control.mapper;

import com.spectra.commons.dto.locator.LocatorDTO;
import com.spectra.control.model.Locator;
import org.springframework.stereotype.Component;

@Component
public class LocatorMapper {
    public LocatorDTO toDto(Locator locator) {
        return new LocatorDTO(
                locator.getType(),
                locator.getValue()
        );
    }
}
