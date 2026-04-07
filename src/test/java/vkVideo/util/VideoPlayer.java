package vkVideo.util;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import vkVideo.locators.VideoPlayerLocators;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

public class VideoPlayer {

    private final AndroidDriver driver;
    private final WebDriverWait wait;
    private static final int TIMEOUT_SEC = 30;
    private static final int SHORT_SLEEP_MS = 1000;
    private static final int LONG_SLEEP_MS = 3000;

    public VideoPlayer(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void skipAuthIfNeeded() {
        findAndClick(VideoPlayerLocators.SKIP_AUTH, SHORT_SLEEP_MS);
    }

    public void closeNotificationIfNeeded() {
        findAndClick(VideoPlayerLocators.CLOSE_NOTIFICATION, 0);
    }

    public void waitForIt() {
        Instant deadline = Instant.now().plus(Duration.ofSeconds(TIMEOUT_SEC));
        while (Instant.now().isBefore(deadline)) {
            closeNotificationIfNeeded();
            skipAuthIfNeeded();
            if (!driver.findElements(VideoPlayerLocators.FIRST_VIDEO).isEmpty()) {
                return;
            }
            sleep(SHORT_SLEEP_MS);
        }
        throw new IllegalStateException("Лента не загрузилась за " + TIMEOUT_SEC + " секунд");
    }

    public void openFirstVideo() {
        List<WebElement> previews = driver.findElements(VideoPlayerLocators.FIRST_VIDEO);
        if (previews.isEmpty()) {
            throw new IllegalStateException("Нет видео в ленте");
        }
        WebElement first = previews.get(0);
        try {
            first.click();
        } catch (Exception e) {
            tapAtCenter(first.getRect());
        }
        sleep(LONG_SLEEP_MS);
    }


    public PlaybackResult checkPlayback() {
        if (isPauseVisible()) {
            return PlaybackResult.IS_PLAYING;
        }
        tapAtUpperQuarter();
        sleep(1000);
        if (isPauseVisible()) {
            return PlaybackResult.IS_PLAYING;
        }
        return PlaybackResult.IS_NOT_PLAYING;
    }

    private void findAndClick(org.openqa.selenium.By locator, int sleepAfterMs) {
        List<WebElement> elements = driver.findElements(locator);
        if (!elements.isEmpty()) {
            elements.get(0).click();
            if (sleepAfterMs > 0) sleep(sleepAfterMs);
        }
    }

    private boolean isPauseVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(VideoPlayerLocators.PAUSE_BUTTON)) != null;
        } catch (Exception e) {
            return false;
        }
    }

    private void tapAtCenter(Rectangle rect) {
        int x = rect.x + rect.width / 2;
        int y = rect.y + rect.height / 2;
        performTap(x, y);
    }

    private void tapAtUpperQuarter() {
        Dimension size = driver.manage().window().getSize();
        int x = size.width / 2;
        int y = size.height / 4; // верхняя четверть
        performTap(x, y);
    }

    private void performTap(int x, int y) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);
        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(tap));
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public enum PlaybackResult {
        IS_PLAYING, IS_NOT_PLAYING
    }
}