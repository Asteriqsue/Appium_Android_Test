package alchemy.config;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.net.URL;

public class DriverSetup {

    private static AndroidDriver driver;

    public static void setup() {
        try {
            UiAutomator2Options options = new UiAutomator2Options();

            options.setPlatformName("Android");
            options.setPlatformVersion("11");
            options.setDeviceName("Android_11_API_30");
            options.setAutomationName("UiAutomator2");
            options.setAppPackage("com.ilyin.alchemy");
            options.setNoReset(false);

            driver = new AndroidDriver(
                    new URL("http://127.0.0.1:4723"),
                    options
            );

        } catch (Exception e) {
            throw new RuntimeException("Driver init failed", e);
        }
    }

    public static AndroidDriver getDriver() {
        return driver;
    }

    public static void resetApp() {
        try {
            Runtime.getRuntime().exec("adb shell pm clear com.ilyin.alchemy");
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void quit() {
        if (driver != null) {
            driver.quit();
        }
    }
}