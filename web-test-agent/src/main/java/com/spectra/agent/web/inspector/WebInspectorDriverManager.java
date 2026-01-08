package com.spectra.agent.web.inspector;

import com.spectra.agent.web.engine.factory.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class WebInspectorDriverManager {

    private final AtomicReference<WebDriver> ref = new AtomicReference<>();

    public synchronized WebDriver createOrReplace(Map<String, Object> config) {
        closeIfPresent();
        WebDriver driver = WebDriverFactory.create(config == null ? Map.of() : config);
        ref.set(driver);
        return driver;
    }

    public WebDriver getOrThrow() {
        WebDriver d = ref.get();
        if (d == null) throw new IllegalStateException("Inspector driver is not initialized. Create a session first.");
        return d;
    }

    public synchronized void closeIfPresent() {
        WebDriver d = ref.getAndSet(null);
        if (d != null) {
            try { d.quit(); } catch (Exception ignored) {}
        }
    }
}