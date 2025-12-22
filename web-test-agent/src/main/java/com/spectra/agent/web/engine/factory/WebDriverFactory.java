package com.spectra.agent.web.engine.factory;

import com.spectra.commons.util.SafeConvert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URI;
import java.net.URL;
import java.util.Map;

public class WebDriverFactory {
    public static WebDriver create(Map<String, Object> config) {
        String browser = SafeConvert.toString(config, "browser", "chrome").toLowerCase();
        Boolean headless = SafeConvert.toBoolean(config, "headless", false);

        String remoteUrlFromConfig = SafeConvert.toString(config, "remoteUrl");
        String remoteUrlFromEnv = switch (browser) {
            case "firefox" -> System.getenv("SELENIUM_URL_FIREFOX");
            default        -> System.getenv("SELENIUM_URL_CHROME");
        };

        String remoteUrl = (remoteUrlFromConfig != null && !remoteUrlFromConfig.isBlank())
                ? remoteUrlFromConfig
                : remoteUrlFromEnv;

        try {
            if (remoteUrl != null && !remoteUrl.isBlank()) {
                URL grid = URI.create(remoteUrl).toURL();
                return switch (browser) {
                    case "chrome" -> buildRemoteChrome(grid, headless);
                    case "firefox" -> buildRemoteFirefox(grid, headless);
                    default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
                };
            }
            else {
                return switch (browser) {
                    case "chrome" -> buildLocalChrome(headless);
                    case "firefox" -> buildLocalFirefox(headless);
                    default -> throw new IllegalArgumentException("Unsupported browser for local: " + browser);
                };
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to init RemoteWebDriver", e);
        }
    }

    private static WebDriver buildRemoteChrome(URL grid, boolean headless) {
        ChromeOptions opts = new ChromeOptions();
        if (headless) {
            opts.addArguments("--headless=new");
        }
        opts.addArguments("--no-sandbox", "--disable-dev-shm-usage");
        return new RemoteWebDriver(grid, opts);
    }

    private static WebDriver buildRemoteFirefox(URL grid, boolean headless) {
        FirefoxOptions opts = new FirefoxOptions();
        if (headless) {
            opts.addArguments("-headless");
        }
        return new RemoteWebDriver(grid, opts);
    }

    private static WebDriver buildLocalChrome(boolean headless) {
        ChromeOptions opts = new ChromeOptions();
        if (headless) {
            opts.addArguments("--headless=new");
        }
        opts.addArguments("--no-sandbox", "--disable-dev-shm-usage");
        return new ChromeDriver(opts);
    }

    private static WebDriver buildLocalFirefox(boolean headless) {
        FirefoxOptions opts = new FirefoxOptions();
        if (headless) {
            opts.addArguments("-headless");
        }
        return new FirefoxDriver(opts);
    }
}
