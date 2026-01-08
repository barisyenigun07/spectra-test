package com.spectra.agent.desktop.inspector.runtime;

import com.spectra.agent.desktop.engine.client.DesktopClient;
import com.spectra.agent.desktop.engine.client.linux.LinuxDesktopClient;
import com.spectra.agent.desktop.engine.client.macos.MacOSDesktopClient;
import com.spectra.agent.desktop.engine.client.windows.WindowsDesktopClient;
import io.appium.java_client.AppiumDriver;
import org.springframework.stereotype.Component;

@Component
public class DesktopRuntimeImpl implements DesktopRuntime {

    private volatile DesktopClient currentClient;

    private final LdtpFacade ldtpFacade;

    public DesktopRuntimeImpl(LdtpFacade ldtpFacade) {
        this.ldtpFacade = ldtpFacade;
    }

    /** Engine burayı set eder */
    public void setClient(DesktopClient client) {
        this.currentClient = client;
    }

    @Override
    public DesktopBackendType backendType() {
        if (currentClient == null) throw new IllegalStateException("Desktop client not initialized");

        return (currentClient instanceof LinuxDesktopClient)
                ? DesktopBackendType.LDTP
                : DesktopBackendType.APPIUM;
    }

    @Override
    public AppiumDriver appiumDriverOrNull() {
        if (currentClient instanceof MacOSDesktopClient mac) return mac.getDriver();
        if (currentClient instanceof WindowsDesktopClient win) return win.getDriver();
        return null;
    }

    @Override
    public LdtpFacade ldtpOrNull() {
        // ✅ Linux ise "bean" facade döndür
        if (currentClient instanceof LinuxDesktopClient) return ldtpFacade;
        return null;
    }
}