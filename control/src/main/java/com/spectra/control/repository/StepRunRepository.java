package com.spectra.control.repository;

import com.spectra.control.model.StepRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StepRunRepository extends JpaRepository<StepRun, Long> {
}
