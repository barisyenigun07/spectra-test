package com.spectra.agent.mobile.mq;

import com.spectra.commons.dto.JobCreatedEvent;
import com.spectra.commons.dto.StepDTO;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.mac.Mac2Driver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URI;

@Component
public class MobileJobListener {
    @RabbitListener(queues = "jobs.mobile")
    public void onMessage(JobCreatedEvent evt) {
        System.out.println("Received JobCreatedEvent ID: " + evt.jobId());
        System.out.println("Received JobCreatedEvent Target Platform:" + evt.targetPlatform());
        System.out.println("=== Steps ===");

        for (StepDTO s : evt.steps()) {
            System.out.println("Order Index: " + s.orderIndex());
            System.out.println("Action: " + s.action());
            System.out.println("Locator Type: " + s.locator().type());
            System.out.println("Locator Value: " + s.locator().value());
        }

        try {
            IOSDriver iosDriver = new IOSDriver(URI.create("").toURL(), new DesiredCapabilities());
            Mac2Driver mac2Driver = new Mac2Driver(URI.create("").toURL(), new DesiredCapabilities());
            var el = iosDriver.findElement(AppiumBy.accessibilityId(""));
            Actions actions = new Actions(iosDriver);
            actions.moveToElement(el).click().sendKeys("Hello World!");

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
