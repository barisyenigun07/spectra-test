package com.spectra.agent.desktop.inspector.runtime;

import com.cobra.ldtp.Ldtp;
import io.appium.java_client.AppiumDriver;

/**
 * Inspector'ın "o anki" desktop sürücüsüne erişmesi için tek nokta.
 * Bunu kendi engine'ine bağla.
 */
public interface DesktopRuntime {

    enum DesktopBackendType {
        APPIUM, // macOS / Windows
        LDTP    // Linux
    }

    DesktopBackendType backendType();

    /**
     * backendType() == APPIUM ise dolu olmalı.
     */
    AppiumDriver appiumDriverOrNull();

    /**
     * backendType() == LDTP ise dolu olmalı.
     * MVP'de placeholder interface.
     */
    LdtpFacade ldtpOrNull();
}