package com.spectra.control.model;

import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Locator {
    @Column(name = "locator_type")
    private String type;
    @Column(name = "value")
    private String value;
}
