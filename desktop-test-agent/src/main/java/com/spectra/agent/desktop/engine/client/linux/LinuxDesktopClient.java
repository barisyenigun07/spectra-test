package com.spectra.agent.desktop.engine.client.linux;

import com.cobra.ldtp.Ldtp;
import com.spectra.agent.desktop.engine.client.DesktopClient;
import com.spectra.commons.dto.locator.LocatorDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LinuxDesktopClient implements DesktopClient {
    @Getter
    private final Ldtp ldtp;

    private int lastX = 0;
    private int lastY = 0;
    private boolean hasLastLocation = false;

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
        if (shortcut == null || shortcut.isBlank()) return;

        String[] parts = shortcut.split("\\+");
        StringBuilder sb = new StringBuilder();

        for (String part : parts) {
            String token = part.trim();
            if (token.isEmpty()) continue;
            sb.append(mapToLdtpKeyToken(token));
        }

        ldtp.generateKeyEvent(sb.toString());
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
    public void moveMouseToElement(LocatorDTO locator) {
        int[] xy = centerOf(locator.value());
        ldtp.generateMouseEvent(xy[0], xy[1]);
        lastX = xy[0];
        lastY = xy[1];
        hasLastLocation = true;
    }

    @Override
    public void moveMouseByOffset(int xOffset, int yOffset) {
        if (!hasLastLocation) {
            lastX = 0;
            lastY = 0;
            hasLastLocation = true;
        }

        lastX += xOffset;
        lastY += yOffset;
        ldtp.generateMouseEvent(lastX, lastY);
    }

    @Override
    public void moveMouseToLocation(int x, int y) {
        ldtp.generateMouseEvent(x, y);
        lastX = x;
        lastY = y;
        hasLastLocation = true;
    }

    @Override
    public void dragAndDropToElement(LocatorDTO from, LocatorDTO to) {
        int[] fromXY = centerOf(from.value());
        int[] toXY = centerOf(to.value());
        dragBetween(fromXY[0], fromXY[1], toXY[0], toXY[1]);
    }

    @Override
    public void dragAndDropByOffset(LocatorDTO el, int xOffset, int yOffset) {
        int[] start = centerOf(el.value());
        int endX = start[0] + xOffset;
        int endY = start[1] + yOffset;
        dragBetween(start[0], start[1], endX, endY);
    }

    @Override
    public void dragAndDropToLocation(LocatorDTO el, int x, int y) {
        int[] start = centerOf(el.value());
        dragBetween(start[0], start[1], x, y);
    }

    @Override
    public boolean isChecked(LocatorDTO locator) {
        return ldtp.check(locator.value()) == 1;
    }

    @Override
    public void scrollUp(LocatorDTO locator, int amount) {
        int n = Math.max(1, amount);
        ldtp.oneUp(locator.value(), n);
    }

    @Override
    public void scrollDown(LocatorDTO locator, int amount) {
        int n = Math.max(1, amount);
        ldtp.oneDown(locator.value(), n);
    }

    @Override
    public void scrollLeft(LocatorDTO locator, int amount) {
        int n = Math.max(1, amount);
        ldtp.oneLeft(locator.value(), n);
    }

    @Override
    public void scrollRight(LocatorDTO locator, int amount) {
        int n = Math.max(1, amount);
        ldtp.oneRight(locator.value(), n);
    }

    @Override
    public void close() {

    }

    private void dragBetween(int startX, int startY, int endX, int endY) {
        ldtp.generateMouseEvent(startX, startY);
        ldtp.generateMouseEvent(startX, startY, "b1p");
        ldtp.simulateMouseMove(startX, startY, endX, endY);
        ldtp.generateMouseEvent(endX, endY, "b1r");

        lastX = endX;
        lastY = endY;
        hasLastLocation = true;
    }

    private int[] centerOf(String objName) {
        Integer[] size = ldtp.getObjectSize(objName);

        if (size == null || size.length == 0) {
            throw new IllegalStateException("Cannot get object size for: " + objName);
        }

        if (size.length >= 4) {
            int x = size[0];
            int y = size[1];
            int w = size[2];
            int h = size[3];
            return new int[]{x + (w / 2), y + (h / 2)};
        }

        throw new IllegalStateException("getObjectSize returned unexpected format (len=" + size.length + ") for: " + objName);
    }

    private String mapToLdtpKeyToken(String token) {
        String t = token.trim().toLowerCase();

        return switch (t) {
            case "ctrl", "control" -> "<ctrl>";
            case "alt", "option" -> "<option>";
            case "shift" -> "<shift>";
            case "enter", "return" -> "<enter>";
            case "tab" -> "<tab>";
            case "esc", "escape" -> "<esc>";
            case "space" -> "<space>";
            case "backspace", "bksp" -> "<bksp>";
            case "delete", "del" -> "<del>";
            case "up" -> "<up>";
            case "down" -> "<down>";
            case "left" -> "<left>";
            case "right" -> "<right>";
            case "home" -> "<home>";
            case "end" -> "<end>";
            case "pageup" -> "<pageup>";
            case "pagedown" -> "<pagedown>";
            default -> {
                if (t.matches("f([1-9]|1[0-2])")) {
                    yield "<" + t + ">";
                }

                if (t.length() == 1) {
                    yield t;
                }

                throw new IllegalArgumentException("Unsupported shortcut token for LDTP!");
            }
        };
    }


}
