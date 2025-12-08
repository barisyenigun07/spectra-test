package com.spectra.agent.desktop.engine;

import com.cobra.ldtp.Ldtp;
import com.spectra.agent.desktop.engine.client.DesktopClient;
import com.spectra.agent.desktop.engine.client.linux.LinuxDesktopClient;
import com.spectra.agent.desktop.engine.client.macos.MacOSDesktopClient;
import com.spectra.agent.desktop.engine.client.windows.WindowsDesktopClient;
import io.appium.java_client.mac.Mac2Driver;
import io.appium.java_client.mac.options.Mac2Options;
import io.appium.java_client.windows.WindowsDriver;
import io.appium.java_client.windows.options.WindowsOptions;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

public class DesktopClientFactory {
    public static DesktopClient create(Map<String, String> config) {
        String rawOsName = config.getOrDefault("os", System.getProperty("os.name"));
        String osName = normalizeOsName(rawOsName);

        return switch (osName) {
            case "mac" -> buildMacClient(config);
            case "windows" -> buildWindowsClient(config);
            case "linux" -> buildLinuxClient(config);
            default -> throw new IllegalArgumentException("Unsupported OS type!");
        };
    }

    private static String normalizeOsName(String osName) {
        String lower = osName.toLowerCase(Locale.ROOT);

        if (lower.contains("mac")) {
            return "mac";
        }
        else if (lower.contains("win")) {
            return "windows";
        }
        else if (lower.contains("nux") || lower.contains("nix") || lower.contains("linux")) {
            return "linux";
        }

        return lower;
    }

    private static MacOSDesktopClient buildMacClient(Map<String, String> config) {
        String bundleId = config.get("bundleId");
        if (bundleId == null || bundleId.isBlank()) {
            throw new IllegalArgumentException("bundleId is required for macOS desktop client");
        }

        Mac2Options options = new Mac2Options()
                .setBundleId(bundleId);
        options.setCapability("appium:automationName", "mac2");
        options.setCapability("platformName", "mac");
        options.setCapability("appium:launchTimeout", 10000);

        URL appiumUrl = buildAppiumUrl(config, "APPIUM_URL");

        Mac2Driver driver = new Mac2Driver(appiumUrl, options);
        return new MacOSDesktopClient(driver);
    }

    private static WindowsDesktopClient buildWindowsClient(Map<String, String> config) {
        String appId = config.get("appId");
        if (appId == null || appId.isBlank()) {
            throw new IllegalArgumentException("appId is required for Windows desktop client");
        }

        WindowsOptions options = new WindowsOptions()
                .setApp(config.get("app"));
        options.setCapability("platformName", "Windows");
        options.setCapability("deviceName", "WindowsPC");
        options.setCapability("ms:waitForAppLaunch", 10);

        URL appiumUrl = buildAppiumUrl(config, "APPIUM_URL");

        WindowsDriver driver = new WindowsDriver(appiumUrl, options);
        return new WindowsDesktopClient(driver);
    }

    private static LinuxDesktopClient buildLinuxClient(Map<String, String> config) {
        String windowName = config.getOrDefault("windowName", "*");
        Ldtp ldtp = new Ldtp(windowName);
        return new LinuxDesktopClient(ldtp);
    }

    private static URL buildAppiumUrl(Map<String, String> config, String envVarName) {
        try {
            String urlFromConfig = config.get("appiumUrl");
            String url = urlFromConfig != null ? urlFromConfig : System.getenv(envVarName);

            if (url == null || url.isBlank()) {
                throw new IllegalStateException("Appium URL is not configured (config['appiumUrl'])");
            }

            return URI.create(url).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid Appium URL", e);
        }
    }
}
