package com.spectra.control.model;

import com.spectra.commons.dto.step.StepStatus;
import com.spectra.control.model.converter.MapToJsonConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Entity
@Table(name = "steps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Step {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "order_index")
    private int orderIndex;
    @Column(name = "action")
    private String action;
    @Column(name = "params", columnDefinition = "jsonb")
    @Convert(converter = MapToJsonConverter.class)
    private Map<String, Object> params;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StepStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_case_id")
    private TestCase testCase;
    @Embedded
    private Locator locator;
}
