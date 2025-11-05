package com.spectra.agent.web.engine;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URI;
import java.net.URL;
import java.util.Map;

public class WebDriverFactory {
    public static WebDriver create(Map<String, String> config) {
        String browser = config.getOrDefault("browser", "chrome").toLowerCase();
        boolean headless = Boolean.parseBoolean(config.getOrDefault("headless", "true"));
        String remoteUrl = switch (browser) {
            case "firefox" -> System.getenv("SELENIUM_URL_FIREFOX");
            default        -> System.getenv("SELENIUM_URL_CHROME");
        };

        if (remoteUrl == null || remoteUrl.isBlank()) {
            throw new IllegalStateException("Selenium URL env not set for " + browser);
        }
        try {
            URL grid = URI.create(remoteUrl).toURL();
            return switch (browser) {
                case "chrome" -> {
                    var opts = new ChromeOptions();
                    if (headless) opts.addArguments("--headless=new");
                    opts.addArguments("--no-sandbox", "--disable-dev-shm-usage");
                    yield new RemoteWebDriver(grid, opts);
                }
                case "firefox" -> {
                    var opts = new FirefoxOptions();
                    if (headless) opts.addArguments("-headless");
                    yield new RemoteWebDriver(grid, opts);
                }
                default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
            };
        } catch (Exception e) {
            throw new RuntimeException("Failed to init RemoteWebDriver", e);
        }
    }
}
