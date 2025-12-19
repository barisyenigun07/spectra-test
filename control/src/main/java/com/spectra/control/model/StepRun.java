package com.spectra.control.model;

import com.spectra.commons.dto.step.StepStatus;
import com.spectra.control.model.converter.MapToJsonConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;

@Entity
@Table(name = "step_runs")
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
    @JoinColumn(name = "test_case_run_id")
    private TestCaseRun testCaseRun;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id")
    private Step step;
    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private StepStatus status;
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
    @Convert(converter = MapToJsonConverter.class)
    @Column(name = "extra", columnDefinition = "jsonb")
    private Map<String, Object> extra;
}
