package com.spectra.agent.mobile;


import io.appium.java_client.mac.Mac2Driver;
import io.appium.java_client.mac.options.Mac2Options;
import io.appium.java_client.windows.WindowsDriver;
import io.appium.java_client.windows.options.WindowsOptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URI;

public class Main {
    public static void main(String[] args) throws MalformedURLException {
        String hello = "Hello World!";
        System.out.println(hello);

        String test = "Hello World!";

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("", "");
        capabilities.setCapability("", "");

        String osName = System.getProperty("os.name").toLowerCase();
        WebDriver driver;

        if (osName.startsWith("mac")) {
            Mac2Options mac2Options = new Mac2Options();
            mac2Options.setCapability("", "");
            mac2Options.setCapability("", "");

            driver = new Mac2Driver(URI.create("").toURL(), mac2Options);
        }

        else if (osName.startsWith("win")) {
            WindowsOptions windowsOptions = new WindowsOptions();
            windowsOptions.setCapability("", "");
            windowsOptions.setCapability("", "");
            driver = new WindowsDriver(URI.create("").toURL(), windowsOptions);
        }

        else if (osName.startsWith("linux")) {
            driver = null;
        }

        else {
            driver = null;
        }

        assert driver != null;

        //WebDriver driver = new WindowsDriver(URI.create("http://localhost:4723").toURL(), capabilities);
        //driver.findElement(By.id("loginBtn")).click();
    }
}