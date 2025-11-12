package com.spectra.agent.mobile.engine;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URI;
import java.net.URL;
import java.util.Map;

public class MobileDriverFactory {
    public static AppiumDriver create(Map<String, String> config) {
        String platform = config.getOrDefault("platform", "android");
        String automation = config.getOrDefault("automationName", "UiAutomator2");
        String device = config.getOrDefault("deviceName", "auto");
        String udid = config.getOrDefault("udid", "auto");

        String remoteUrl = System.getenv("APPIUM_URL");
        if (remoteUrl == null) throw new IllegalStateException("APPIUM_URL must be set!");

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", platform);
        caps.setCapability("automationName", automation);
        caps.setCapability("deviceName", device);
        caps.setCapability("udid", udid);

        if (platform.equals("android")) {
            if (config.containsKey("appPackage")) {
                caps.setCapability("appPackage", config.get("appPackage"));
            }

            if (config.containsKey("appActivity")) {
                caps.setCapability("appActivity", config.get("appActivity"));
            }
        }

        if (platform.equals("ios")) {
            if (config.containsKey("bundleId")) {
                caps.setCapability("bundleId", config.get("bundleId"));
            }

            if (config.containsKey("app")) {
                caps.setCapability("app", config.get("app"));
            }
        }

        try {
            URL server = URI.create(remoteUrl).toURL();
            return platform.equals("ios")
                    ? new IOSDriver(server, caps)
                    : new AndroidDriver(server, caps);
        } catch (Exception e) {
            throw new RuntimeException("Failed to init Appium driver", e);
        }
    }
}
