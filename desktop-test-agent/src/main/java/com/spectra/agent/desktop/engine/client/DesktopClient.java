package com.spectra.agent.desktop.engine.client;

import com.spectra.commons.dto.locator.LocatorDTO;

public interface DesktopClient {
    void click(LocatorDTO locator);
    void doubleClick(LocatorDTO locator);
    void type(LocatorDTO locator, String text);
    void sendShortcut(String shortcut);
    String getText(LocatorDTO locator);
    boolean isVisible(LocatorDTO locator);
    void mouseMove(LocatorDTO locator);
    void dragAndDrop(LocatorDTO locator);
}
