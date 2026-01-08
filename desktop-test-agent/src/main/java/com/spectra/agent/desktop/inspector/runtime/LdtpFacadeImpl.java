package com.spectra.agent.desktop.inspector.runtime;

import com.cobra.ldtp.Ldtp;
import com.spectra.agent.desktop.inspector.DesktopInspectorRuntimeManager;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class LdtpFacadeImpl implements LdtpFacade {

    private final DesktopInspectorRuntimeManager runtimeManager;

    public LdtpFacadeImpl(DesktopInspectorRuntimeManager runtimeManager) {
        this.runtimeManager = runtimeManager;
    }

    private Ldtp ldtp() {
        var rt = runtimeManager.getOrThrow();
        if (rt.kind() != DesktopInspectorRuntimeManager.BackendKind.LDTP || rt.ldtp() == null) {
            throw new IllegalStateException("LDTP runtime is not initialized. Create a DESKTOP session on Linux first.");
        }
        return rt.ldtp();
    }

    @Override
    public String activeWindowTitle() {
        try { return ldtp().getWindowName(); }
        catch (Exception e) { return ""; }
    }

    @Override
    public List<LdtpObject> listObjects() {
        try {
            String[] ids = ldtp().getObjectList(); // ✅ String[] dönüyor
            if (ids == null || ids.length == 0) return List.of();

            List<LdtpObject> out = new ArrayList<>(ids.length);

            for (String idRaw : ids) {
                String id = (idRaw == null) ? "" : idRaw.trim();
                if (id.isBlank()) continue;

                // MVP: role/name yoksa boş bırak
                String name = id;       // istersen name=id yap (UI’da daha anlamlı oluyor)
                String role = "object"; // default

                // Eğer sende bu tarz API’ler varsa, burada doldurabilirsin:
                // name = safeTry(() -> ldtp().getObjectName(id), name);
                // role = safeTry(() -> ldtp().getObjectRole(id), role);

                out.add(new LdtpObject(id, name, role));
            }

            return out;
        } catch (Exception e) {
            return List.of();
        }
    }
}
