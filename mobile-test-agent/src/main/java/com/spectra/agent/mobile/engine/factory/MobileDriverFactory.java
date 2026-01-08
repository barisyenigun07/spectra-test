package com.spectra.agent.mobile.engine.factory;

import com.spectra.commons.util.SafeConvert;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;

public class MobileDriverFactory {
    public static AppiumDriver create(Map<String, Object> config) {
        String platform = SafeConvert.toString(config, "platformName", "android");
        //String automation = SafeConvert.toString(config, "automationName", "UiAutomator2");
        String device = SafeConvert.toString(config, "deviceName", "").trim();
        String udid = SafeConvert.toString(config, "udid", null);
        String platformVersion = SafeConvert.toString(config, "platformVersion", null);

        String remoteUrl = System.getenv("APPIUM_URL");
        if (remoteUrl == null) throw new IllegalStateException("APPIUM_URL must be set!");

        URL server;

        try {
            server = URI.create(remoteUrl).toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid APPIUM_URL", e);
        }

        if (platform.equals("android")) {
            UiAutomator2Options opts = new UiAutomator2Options();
            opts.setPlatformName("Android");
            opts.setAutomationName("UiAutomator2");
            if (!device.isEmpty()) opts.setDeviceName(device);
            if (udid != null && !udid.isBlank()) opts.setUdid(udid);
            if (platformVersion != null && !platformVersion.isBlank()) opts.setPlatformVersion(platformVersion);

            String appActivity = SafeConvert.toString(config, "appActivity", null);
            String appPackage = SafeConvert.toString(config, "appPackage", null);
            String app = SafeConvert.toString(config, "app", null);

            if (app != null && !app.isBlank()) opts.setApp(app);
            if (appActivity != null && !appActivity.isBlank()) opts.setAppActivity(appActivity);
            if (appPackage != null && !appPackage.isBlank()) opts.setAppPackage(appPackage);

            return new AndroidDriver(server, opts);
        }

        if (platform.equals("ios")) {
            XCUITestOptions opts = new XCUITestOptions();
            opts.setPlatformName("iOS");
            opts.setAutomationName("XCUITest");

            if (!device.isEmpty()) opts.setDeviceName(device);
            if (udid != null && !udid.isBlank()) opts.setUdid(udid);
            if (platformVersion != null && !platformVersion.isBlank()) opts.setPlatformVersion(platformVersion);

            String app = SafeConvert.toString(config, "app", null);
            String bundleId = SafeConvert.toString(config, "bundleId", null);

            if (app != null && !app.isBlank()) opts.setApp(app);
            else if (bundleId != null && !bundleId.isBlank()) opts.setBundleId(bundleId);
            else throw new IllegalArgumentException("iOS requires app or bundleId");

            return new IOSDriver(server, opts);
        }

        return null;
    }
}
