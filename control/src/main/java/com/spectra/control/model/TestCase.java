package com.spectra.control.model;

import com.spectra.commons.dto.testcase.TestCaseStatus;
import com.spectra.control.model.converter.MapToJsonConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "test_cases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "target_platform")
    private String targetPlatform;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private TestCaseStatus status;
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
    @OneToMany(mappedBy = "testCase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Step> steps;
    @Column(name = "config", columnDefinition = "jsonb")
    @Convert(converter = MapToJsonConverter.class)
    private Map<String, Object> config;
}
