import { SchemaDef } from "./schema";

export const PLATFORM_CONFIG_SCHEMAS: Record<"web"|"mobile"|"desktop", SchemaDef> = {
  web: {
    title: "Web Config",
    fields: [
      { key: "browser", label: "Browser", type: "select", defaultValue: "chrome",
        options: [{label:"Chrome",value:"chrome"},{label:"Firefox",value:"firefox"}] },
      { key: "headless", label: "Headless", type: "boolean", defaultValue: false },
      //{ key: "baseUrl", label: "Base URL", type: "string", placeholder: "https://example.com" },
      //{ key: "implicitWaitMs", label: "Implicit Wait (ms)", type: "number", defaultValue: 0 },
    ],
  },

  mobile: {
    title: "Mobile Config",
    fields: [
      { key: "platformName", label: "Platform", type: "select", defaultValue: "android",
        options: [{label:"Android",value:"android"},{label:"iOS",value:"ios"}] },
      { key: "deviceName", label: "Device Name", type: "string", placeholder: "emulator-5554 / iPhone 15" },
      { key: "udid", label: "UDID", type: "string" },
      { key: "app", label: "App Path/URL", type: "string", placeholder: "/path/app.apk or storage URL", condition: {dependsOn: "platformName", equals: "ios"} },
      { key: "bundleId", label: "Bundle Id", type: "string", placeholder: "Bundle Id", condition: {dependsOn: "platformName", equals: "ios"} },
      { key: "appPackage", label: "appPackage", type: "string", condition: {dependsOn: "platformName", equals: "android"} },
      { key: "appActivity", label: "appActivity", type: "string", condition: {dependsOn: "platformName", equals: "android"} },
    ],
  },

  desktop: {
    title: "Desktop Config",
    fields: [
      { key: "os", label: "OS", type: "select", defaultValue: "mac",
        options: [{label:"macOS",value:"mac"},{label:"windows",value:"windows"},{label:"linux",value:"linux"}] },
      { key: "app", label: "App", type: "string", placeholder: "C:\\Program Files\\MyApp\\MyApp.exe  OR  Microsoft.WindowsCalculator_...!App", condition: {dependsOn: "os", equals: "windows"} },
      { key: "bundleId", label: "Bundle Id", type: "string", condition: {dependsOn: "os", equals: "mac"} },
      { key: "windowName", label: "Window Name", type: "string", condition: {dependsOn: "os", equals: "linux"}}
    ],
  },
};