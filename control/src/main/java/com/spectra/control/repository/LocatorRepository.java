package com.spectra.control.repository;

import com.spectra.control.model.Locator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocatorRepository extends JpaRepository<Locator, Long> {
}
