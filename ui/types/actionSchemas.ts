import type { SchemaDef } from "./schema";

type Platform = "web" | "mobile" | "desktop";

const NO_PARAMS = (title: string): SchemaDef => ({ title, fields: [] });

// Action → params schema (paramı olmayanlar fields: [])
export const ACTION_PARAM_SCHEMAS: Record<Platform, Record<string, SchemaDef>> = {
  web: {
    openUrl: {
      title: "openUrl(url)",
      fields: [
        { key: "url", label: "URL", type: "string", required: true, placeholder: "https://..." },
      ],
    },

    click: NO_PARAMS("click()"),
    doubleClick: NO_PARAMS("doubleClick()"),
    navigateBack: NO_PARAMS("navigateBack()"),
    navigateFront: NO_PARAMS("navigateFront()"),

    type: {
      title: "type(text)",
      fields: [
        { key: "text", label: "Text", type: "string", required: true },
      ],
    },

    assertText: {
      title: "assertText(expectedText)",
      fields: [
        { key: "expectedText", label: "Expected Text", type: "string", required: true },
      ],
    },

    assertElement: NO_PARAMS("assertElement()"),
  },

  mobile: {
    tap: NO_PARAMS("tap()"),

    type: {
      title: "type(text)",
      fields: [
        { key: "text", label: "Text", type: "string", required: true },
      ],
    },

    swipe: {
      title: "swipe(direction, distance)",
      fields: [
        {
          key: "direction",
          label: "Direction",
          type: "select",
          required: true,
          defaultValue: "up",
          options: [
            { label: "Up", value: "up" },
            { label: "Down", value: "down" },
            { label: "Left", value: "left" },
            { label: "Right", value: "right" },
          ],
        },
        { key: "distance", label: "Distance", type: "number", required: true, defaultValue: 300 },
      ],
    },

    swipeUntilVisible: {
      title: "swipeUntilVisible(direction, maxSwipes, distance)",
      fields: [
        {
          key: "direction",
          label: "Direction",
          type: "select",
          required: true,
          defaultValue: "up",
          options: [
            { label: "Up", value: "up" },
            { label: "Down", value: "down" },
            { label: "Left", value: "left" },
            { label: "Right", value: "right" },
          ],
        },
        { key: "maxSwipes", label: "Max Swipes", type: "number", required: true, defaultValue: 5 },
        { key: "distance", label: "Distance", type: "number", required: true, defaultValue: 300 },
      ],
    },

    assertText: {
      title: "assertText(expectedText)",
      fields: [
        { key: "expectedText", label: "Expected Text", type: "string", required: true },
      ],
    },

    assertElement: NO_PARAMS("assertElement()"),
    longPress: NO_PARAMS("longPress()"),
    navigateBack: NO_PARAMS("navigateBack()"),
    navigateFront: NO_PARAMS("navigateFront()"),
  },

  desktop: {
    assertElement: NO_PARAMS("assertElement()"),
    assertIsChecked: NO_PARAMS("assertIsChecked()"),

    assertText: {
      title: "assertText(expectedText)",
      fields: [
        { key: "expectedText", label: "Expected Text", type: "string", required: true },
      ],
    },

    click: NO_PARAMS("click()"),
    doubleClick: NO_PARAMS("doubleClick()"),

    dragAndDropByOffset: {
      title: "dragAndDropByOffset(xOffset, yOffset)",
      fields: [
        { key: "xOffset", label: "X Offset", type: "number", required: true, defaultValue: 0 },
        { key: "yOffset", label: "Y Offset", type: "number", required: true, defaultValue: 0 },
      ],
    },

    dragAndDropToElement: {
      title: "dragAndDropToElement(target: LocatorDTO)",
      fields: [
        {
          key: "target",
          label: "Target Locator (JSON)",
          type: "json",
          required: true,
          defaultValue: { type: "xpath", value: "" },
          help: `LocatorDTO format: {"type":"...","value":"..."}`,
        },
      ],
    },

    dragAndDropToLocation: {
      title: "dragAndDropToLocation(x, y)",
      fields: [
        { key: "x", label: "X", type: "number", required: true },
        { key: "y", label: "Y", type: "number", required: true },
      ],
    },

    moveMouseByOffset: {
      title: "moveMouseByOffset(xOffset, yOffset)",
      fields: [
        { key: "xOffset", label: "X Offset", type: "number", required: true, defaultValue: 0 },
        { key: "yOffset", label: "Y Offset", type: "number", required: true, defaultValue: 0 },
      ],
    },

    moveMouseToElement: NO_PARAMS("moveMouseToElement()"),

    moveMouseToLocation: {
      title: "moveMouseToLocation(x, y)",
      fields: [
        { key: "x", label: "X", type: "number", required: true },
        { key: "y", label: "Y", type: "number", required: true },
      ],
    },

    sendShortcut: {
      title: "sendShortcut(shortcut)",
      fields: [
        { key: "shortcut", label: "Shortcut", type: "string", required: true, placeholder: "Cmd+S / Ctrl+S" },
      ],
    },

    scroll: {
      title: "scroll(direction, amount)",
      fields: [
        {
          key: "direction",
          label: "Direction",
          type: "select",
          required: true,
          defaultValue: "down",
          options: [
            { label: "Down", value: "down" },
            { label: "Up", value: "up" },
            { label: "Left", value: "left" },
            { label: "Right", value: "right" },
          ],
        },
        { key: "amount", label: "Amount", type: "number", required: true, defaultValue: 500 },
      ],
    },

    type: {
      title: "type(text)",
      fields: [
        { key: "text", label: "Text", type: "string", required: true },
      ],
    },
  },
};