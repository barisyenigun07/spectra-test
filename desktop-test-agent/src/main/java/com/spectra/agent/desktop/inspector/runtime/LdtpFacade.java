package com.spectra.agent.desktop.inspector.runtime;

import java.util.List;

public interface LdtpFacade {

    record LdtpObject(String id, String name, String role) {}

    String activeWindowTitle();

    /**
     * MVP: hiyerarşi yok -> düz liste
     */
    List<LdtpObject> listObjects();
}