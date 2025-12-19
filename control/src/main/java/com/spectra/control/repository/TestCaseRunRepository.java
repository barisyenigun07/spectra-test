package com.spectra.control.repository;


import com.spectra.control.model.TestCaseRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestCaseRunRepository extends JpaRepository<TestCaseRun, Long> {
    List<TestCaseRun> findByTestCaseIdOrderByStartedAtDesc(Long testCaseId);
    Optional<TestCaseRun> findTopByTestCaseIdOrderByStartedAtDesc(Long testCaseId);
}
