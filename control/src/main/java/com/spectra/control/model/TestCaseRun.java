package com.spectra.control.model;

import com.spectra.commons.dto.testcase.TestCaseStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "test_case_runs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_case_id")
    private TestCase testCase;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TestCaseStatus status;
    @Column(name = "started_at")
    private Instant startedAt;
    @Column(name = "finished_at")
    private Instant finishedAt;
    @Column(name = "duration_millis")
    private long durationMillis;
    @Column(name = "failed_steps")
    private int failedSteps;
    @Column(name = "passed_steps")
    private int passedSteps;
    @Column(name = "skipped_steps")
    private int skippedSteps;
    @OneToMany(mappedBy = "testCaseRun", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StepRun> stepRuns;
}
