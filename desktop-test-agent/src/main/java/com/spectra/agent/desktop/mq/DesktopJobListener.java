package com.spectra.agent.desktop.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.spectra.commons.dto.JobCreatedEvent;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.mac.options.Mac2Options;
import io.appium.java_client.windows.WindowsDriver;
import org.openqa.selenium.By;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;

@Component
public class DesktopJobListener {
    @RabbitListener(queues = "jobs.desktop")
    public void onMessage(JobCreatedEvent evt) throws MalformedURLException {
        System.out.println("Received JobCreatedEvent ID: " + evt.jobId());
        System.out.println("Received JobCreatedEvent Target: " + evt.targetPlatform());

        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Windows");
        options.setDeviceName("WindowsPC");
        options.setApp("C:\\Windows\\Desktop\\notepad.exe");

        AppiumDriver driver = new WindowsDriver(URI.create("http://localhost:4723").toURL(), options);
        driver.findElement(By.name("Mesaj Alanı")).sendKeys("Merhaba");
        driver.findElement(AppiumBy.accessibilityId(""));

        Mac2Options mac2Options = new Mac2Options();
        mac2Options.setPlatformName("");
    }
}
