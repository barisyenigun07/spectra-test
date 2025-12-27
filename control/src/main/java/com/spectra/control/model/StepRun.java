package com.spectra.control.model;

import com.spectra.commons.dto.step.StepStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

@Entity
@Table(
        name = "step_runs",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_step_run_testcase_step",
                        columnNames = {"test_case_run_id", "step_id"}
                )
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StepRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_case_run_id", nullable = false)
    private TestCaseRun testCaseRun;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = false)
    private Step step;
    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private StepStatus status;
    @Column(name = "message")
    private String message;
    @Column(name = "started_at")
    private Instant startedAt;
    @Column(name = "finished_at")
    private Instant finishedAt;
    @Column(name = "duration_millis")
    private long durationMillis;
    @Column(name = "error_type")
    private String errorType;
    @Column(name = "error_message")
    private String errorMessage;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extra", columnDefinition = "jsonb")
    private Map<String, Object> extra;
}
