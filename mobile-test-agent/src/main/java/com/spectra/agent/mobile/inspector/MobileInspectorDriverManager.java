package com.spectra.agent.mobile.inspector;

import com.spectra.agent.mobile.engine.factory.MobileDriverFactory;
import io.appium.java_client.AppiumDriver;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class MobileInspectorDriverManager {

    private final AtomicReference<AppiumDriver> ref = new AtomicReference<>();

    public synchronized AppiumDriver createOrReplace(Map<String, Object> config) {
        closeIfPresent();
        AppiumDriver driver = MobileDriverFactory.create(config == null ? Map.of() : config);
        ref.set(driver);
        return driver;
    }

    public AppiumDriver getOrThrow() {
        AppiumDriver d = ref.get();
        if (d == null) throw new IllegalStateException("Inspector driver is not initialized. Create a session first.");
        return d;
    }

    public synchronized void closeIfPresent() {
        AppiumDriver d = ref.getAndSet(null);
        if (d != null) {
            try { d.quit(); } catch (Exception ignored) {}
        }
    }
}