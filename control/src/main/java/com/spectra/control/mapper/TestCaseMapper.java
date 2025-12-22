package com.spectra.control.mapper;

import com.spectra.commons.dto.testcase.TestCaseDTO;
import com.spectra.commons.dto.testcase.TestCaseResultDTO;
import com.spectra.control.model.TestCase;
import com.spectra.control.model.TestCaseRun;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class TestCaseMapper{
    private final StepMapper stepMapper;

    public TestCaseDTO toDto(TestCase entity) {
        return new TestCaseDTO(
                entity.getId(),
                entity.getTargetPlatform(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getSteps()
                        .stream()
                        .map(stepMapper::toDto)
                        .toList(),
                entity.getConfig()
        );
    }

    public TestCaseResultDTO toResultDto(TestCaseRun entity) {
        return new TestCaseResultDTO(
                entity.getTestCase().getId(),
                entity.getId(),
                entity.getTestCase().getTargetPlatform(),
                entity.getStatus(),
                entity.getStartedAt(),
                entity.getFinishedAt(),
                Duration.between(entity.getStartedAt(), entity.getFinishedAt()).toMillis(),
                entity.getStepRuns().stream().map(stepMapper::toResultDTO).toList()
        );
    }
}
