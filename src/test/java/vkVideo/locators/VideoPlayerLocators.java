package vkVideo.locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;


public class VideoPlayerLocators {
    public static final By CLOSE_NOTIFICATION = AppiumBy.id("com.vk.vkvideo:id/close_btn_left");
    public static final By SKIP_AUTH = AppiumBy.id("com.vk.vkvideo:id/fast_login_tertiary_btn");
    public static final By FIRST_VIDEO = AppiumBy.id
            ("com.vk.vkvideo:id/preview");
    public static final By PLAY_BUTTON = AppiumBy.id("com.vk.vkvideo:id/play");
    public static final By PAUSE_BUTTON = AppiumBy.accessibilityId("Pause");
}