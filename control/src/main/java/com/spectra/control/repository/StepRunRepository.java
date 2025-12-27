package com.spectra.control.repository;

import com.spectra.control.model.StepRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StepRunRepository extends JpaRepository<StepRun, Long> {
    Optional<StepRun> findByTestCaseRunIdAndStepId(Long runId, Long stepId);
}
