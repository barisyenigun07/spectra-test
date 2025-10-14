package com.spectra.control.service;

import com.spectra.control.model.Step;
import com.spectra.control.repository.StepRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StepService {
    private final StepRepository stepRepository;

    public void createStep(Step step) {
        stepRepository.save(step);
    }

    public Step getStep(Long id) {
        return stepRepository.findById(id).orElse(null);
    }

    public List<Step> getSteps() {
        return stepRepository.findAll();
    }


}
