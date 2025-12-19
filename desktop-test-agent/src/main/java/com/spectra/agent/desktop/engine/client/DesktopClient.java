package com.spectra.agent.desktop.engine.client;

import com.spectra.commons.dto.locator.LocatorDTO;

public interface DesktopClient {
    void click(LocatorDTO locator);
    void doubleClick(LocatorDTO locator);
    void type(LocatorDTO locator, String text);
    void sendShortcut(String shortcut);
    String getText(LocatorDTO locator);
    boolean isVisible(LocatorDTO locator);
    void moveMouseToElement(LocatorDTO locator);
    void moveMouseByOffset(int xOffset, int yOffset);
    void moveMouseToLocation(int x, int y);
    void dragAndDropToElement(LocatorDTO from, LocatorDTO to);
    void dragAndDropByOffset(LocatorDTO el, int xOffset, int yOffset);
    void dragAndDropToLocation(LocatorDTO el, int x, int y);
    boolean isChecked(LocatorDTO locator);
    default void check(LocatorDTO locator) {
        if (!isChecked(locator)) click(locator);
    }
    default void uncheck(LocatorDTO locator) {
        if (isChecked(locator)) click(locator);
    }
    void scrollUp(LocatorDTO locator, int amount);
    void scrollDown(LocatorDTO locator, int amount);
    void scrollLeft(LocatorDTO locator, int amount);
    void scrollRight(LocatorDTO locator, int amount);
}
