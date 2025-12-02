package com.spectra.agent.desktop.engine;

import com.spectra.agent.desktop.engine.client.DesktopClient;
import com.spectra.agent.desktop.engine.client.linux.LinuxDesktopClient;
import com.spectra.agent.desktop.engine.client.macos.MacOSDesktopClient;
import com.spectra.agent.desktop.engine.client.windows.WindowsDesktopClient;
import io.appium.java_client.mac.options.Mac2Options;

import java.util.Map;

public class DesktopClientFactory {
    public static DesktopClient create(Map<String, String> config) {
        String osName = System.getProperty("os.name").toLowerCase();

        return switch (osName) {
            case "mac" -> buildMacClient(config);
            case "windows" -> buildWindowsClient(config);
            case "linux" -> buildLinuxClient(config);
            default -> throw new IllegalArgumentException("Unsupported OS type!");
        };
    }

    private static MacOSDesktopClient buildMacClient(Map<String, String> config) {
        Mac2Options options = new Mac2Options();

        return null;
    }

    private static WindowsDesktopClient buildWindowsClient(Map<String, String> config) {
        return null;
    }

    private static LinuxDesktopClient buildLinuxClient(Map<String, String> config) {
        return null;
    }
}
