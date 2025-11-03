package com.spectra.agent.web.engine;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.util.Map;

public class WebDriverFactory {
    public static WebDriver create(Map<String, String> config) {
        String browser = config.getOrDefault("browser", "chrome").toLowerCase();
        boolean isHeadless = Boolean.parseBoolean(config.getOrDefault("headless", "false"));
        String remoteUrl = config.get("remoteUrl");

        return switch (browser) {
            case "chrome" -> buildChrome();
            case "firefox" -> buildFirefox();
            case "edge" -> buildEdge();
            case "safari" -> buildSafari();
            default -> throw new IllegalArgumentException("Unsupported browser type");
        };
    }

    private static WebDriver buildChrome() {
        return new ChromeDriver();
    }

    private static WebDriver buildFirefox() {
        return new FirefoxDriver();
    }

    private static WebDriver buildEdge() {
        return new EdgeDriver();
    }

    private static WebDriver buildSafari() {
        return new SafariDriver();
    }
}
