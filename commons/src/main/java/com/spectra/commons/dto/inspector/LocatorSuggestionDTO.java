package com.spectra.commons.dto.inspector;

public record LocatorSuggestionDTO(
        String strategy, // id | css | xpath
        String value,
        double score,
        String note
) {}