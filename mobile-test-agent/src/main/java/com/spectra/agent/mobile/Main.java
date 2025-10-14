package com.spectra.agent.mobile;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class Main {
    public static void main(String[] args) {
        String hello = "Hello World!";
        System.out.println(hello);

        String test = "Hello World!";

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("", "");
        capabilities.setCapability("", "");
    }
}