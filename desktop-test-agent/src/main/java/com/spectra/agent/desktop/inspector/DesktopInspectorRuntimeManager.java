package com.spectra.agent.desktop.inspector;

import com.cobra.ldtp.Ldtp;
import com.spectra.agent.desktop.engine.client.DesktopClient;
import com.spectra.agent.desktop.engine.client.linux.LinuxDesktopClient;
import com.spectra.agent.desktop.engine.factory.DesktopClientFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class DesktopInspectorRuntimeManager {

    public enum BackendKind { APPIUM, LDTP }

    // runtime: Linux ise ldtp dolu, diğerlerinde null
    public record Runtime(BackendKind kind, DesktopClient client, Ldtp ldtp) {}

    private final AtomicReference<Runtime> ref = new AtomicReference<>();

    public synchronized Runtime createOrReplace(Map<String, Object> config) {
        closeIfPresent();

        DesktopClient client = DesktopClientFactory.create(config == null ? Map.of() : config);

        if (client instanceof LinuxDesktopClient linux) {
            Ldtp ldtp = linux.getLdtp(); // LinuxDesktopClient -> Ldtp
            Runtime rt = new Runtime(BackendKind.LDTP, client, ldtp);
            ref.set(rt);
            return rt;
        }

        Runtime rt = new Runtime(BackendKind.APPIUM, client, null);
        ref.set(rt);
        return rt;
    }

    public Runtime getOrThrow() {
        Runtime r = ref.get();
        if (r == null) throw new IllegalStateException("Inspector runtime is not initialized. Create a session first.");
        return r;
    }

    public synchronized void closeIfPresent() {
        Runtime r = ref.getAndSet(null);
        if (r == null) return;

        try {
            if (r.client() != null) r.client().close();
        } catch (Exception ignored) {}
    }
}