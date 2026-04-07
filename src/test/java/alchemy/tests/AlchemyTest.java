package alchemy.tests;

import alchemy.config.DriverSetup;
import alchemy.pages.MainPage;
import org.junit.jupiter.api.*;

public class AlchemyTest {

    private static MainPage mainPage;

    @BeforeAll
    static void setup() {
        DriverSetup.resetApp();
        DriverSetup.setup();
        mainPage = new MainPage(DriverSetup.getDriver());
    }

    @Test
    @DisplayName("Увеличение количества подсказок через просмотр рекламы")
    void testHintsAfterAd() {
        mainPage.clickPlay();
        mainPage.openHintsMenu();
        mainPage.watchAdForHints();

        Assertions.assertEquals(
                4,
                mainPage.getHintsCount(),
                "Ожидалось 4 подсказки"

        );
        System.out.println("Количество подсказок увеличилось до 4: ");
    }

    @AfterAll
    public static void tearDown() {
            DriverSetup.quit();
    }
}