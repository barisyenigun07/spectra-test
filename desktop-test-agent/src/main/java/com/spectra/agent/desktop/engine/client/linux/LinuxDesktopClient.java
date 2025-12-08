package com.spectra.agent.desktop.engine.client.linux;

import com.cobra.ldtp.Ldtp;
import com.spectra.agent.desktop.engine.client.DesktopClient;
import com.spectra.commons.dto.locator.LocatorDTO;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LinuxDesktopClient implements DesktopClient {
    private final Ldtp ldtp;

    @Override
    public void click(LocatorDTO locator) {
        ldtp.click(locator.value());
    }

    @Override
    public void doubleClick(LocatorDTO locator) {
        ldtp.doubleClick(locator.value());
    }

    @Override
    public void type(LocatorDTO locator, String text) {
        ldtp.enterString(locator.value(), text);
    }

    @Override
    public void sendShortcut(String shortcut) {
        ldtp.generateKeyEvent(shortcut);
    }

    @Override
    public String getText(LocatorDTO locator) {
        return ldtp.getTextValue(locator.value());
    }

    @Override
    public boolean isVisible(LocatorDTO locator) {
        return ldtp.objectExist(locator.value()) != 0;
    }

    @Override
    public void mouseMove(LocatorDTO locator) {
        ldtp.mouseMove(locator.value());
    }

    @Override
    public void dragAndDrop(LocatorDTO locator) {

    }

}
