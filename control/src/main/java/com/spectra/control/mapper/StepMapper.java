package com.spectra.control.mapper;

import com.spectra.commons.dto.step.StepDTO;
import com.spectra.commons.dto.step.StepResultDTO;
import com.spectra.control.model.Step;
import com.spectra.control.model.StepRun;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StepMapper {
    private final LocatorMapper locatorMapper;

    public StepDTO toDto(Step entity) {
        return new StepDTO(
                entity.getId(),
                entity.getOrderIndex(),
                entity.getAction(),
                entity.getLocator() == null ? null : locatorMapper.toDto(entity.getLocator()),
                entity.getParams()
        );
    }

    public StepResultDTO toResultDTO(StepRun entity) {
        return new StepResultDTO(
                entity.getStep().getId(),
                entity.getTestCaseRun().getId(),
                entity.getStep().getOrderIndex(),
                entity.getStep().getAction(),
                entity.getStep().getLocator() == null ? null : locatorMapper.toDto(entity.getStep().getLocator()),
                entity.getStatus(),
                entity.getMessage(),
                entity.getStartedAt(),
                entity.getFinishedAt(),
                entity.getDurationMillis(),
                entity.getErrorMessage(),
                entity.getErrorType(),
                entity.getExtra()
        );
    }
}
