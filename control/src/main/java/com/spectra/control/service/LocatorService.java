package com.spectra.control.service;

import com.spectra.control.repository.LocatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocatorService {
    private final LocatorRepository locatorRepository;

    public void createLocator() {

    }
}
