package com.spectra.control.mapper;

import com.spectra.commons.dto.testcase.TestCaseDTO;
import com.spectra.control.model.TestCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
                        .toList()
        );
    }
}
