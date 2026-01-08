package com.spectra.control.mapper;

import com.spectra.commons.dto.step.StepResultDTO;
import com.spectra.commons.dto.testcase.TestCaseDTO;
import com.spectra.commons.dto.testcase.TestCaseResultDTO;
import com.spectra.control.model.TestCase;
import com.spectra.control.model.TestCaseRun;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TestCaseMapper{
    private final StepMapper stepMapper;

    public TestCaseDTO toDto(TestCase entity) {
        return new TestCaseDTO(
                entity.getId(),
                entity.getTargetPlatform(),
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
        Instant started = entity.getStartedAt();
        Instant finished = entity.getFinishedAt();

        long durationMillis = 0L;
        if (started != null) {
            Instant end = (finished != null) ? finished : Instant.now();
            durationMillis = Duration.between(started, end).toMillis();
        }

        return new TestCaseResultDTO(
                entity.getTestCase().getId(),
                entity.getId(),
                entity.getTestCase().getTargetPlatform(),
                entity.getStatus(),
                started,
                finished,
                durationMillis,
                (entity.getStepRuns() == null ? List.<StepResultDTO>of() : entity.getStepRuns().stream().map(stepMapper::toResultDTO).toList())
        );
    }
}
