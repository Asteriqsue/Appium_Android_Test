package vkVideo.tests;

import vkVideo.config.DriverSetup;
import vkVideo.util.VideoPlayer;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VkVideoTest {

    private VideoPlayer videoPlayer;
    private static final String APP_PACKAGE = "com.vk.vkvideo";

    @BeforeAll
    public static void setup() throws Exception {
        DriverSetup.configure();
        clearAppData();
    }

    @BeforeEach
    public void initPage() {
        DriverSetup.getDriver().terminateApp(APP_PACKAGE);
        DriverSetup.getDriver().activateApp(APP_PACKAGE);
        sleep(3000); // даём приложению полностью запуститься

        videoPlayer = new VideoPlayer(DriverSetup.getDriver());
        videoPlayer.waitForIt();
    }

    @Test
    @DisplayName("В")
    public void videoShouldPlaySuccessfully() {
        videoPlayer.openFirstVideo();
        VideoPlayer.PlaybackResult result = videoPlayer.checkPlayback();

        assertEquals(VideoPlayer.PlaybackResult.IS_PLAYING, result,
                "Видео не воспроизводится");
        System.out.println("Видео успешно воспроизводится.");
    }

    @AfterEach
    public void cleanUp() {
        clearAppData();
    }

    @AfterAll
    public static void tearDown() {
        DriverSetup.teardown();
    }

    private static void clearAppData() {
        try {
            Runtime.getRuntime().exec("adb shell pm clear " + APP_PACKAGE);
            sleep(2000);
        } catch (Exception e) {
            System.err.println("Не удалось очистить данные: " + e.getMessage());
        }
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}