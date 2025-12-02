package com.spectra.agent.desktop.engine.client.linux;

import com.cobra.ldtp.Ldtp;
import com.spectra.agent.desktop.engine.client.DesktopClient;
import com.spectra.commons.dto.LocatorDTO;


public class LinuxDesktopClient implements DesktopClient {
    private Ldtp ldtp;

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
        
    }

    @Override
    public String getText(LocatorDTO locator) {
        return "";
    }

    @Override
    public boolean isVisible(LocatorDTO locator) {
        return ldtp.objectExist(locator.value()) != 0;
    }

}
