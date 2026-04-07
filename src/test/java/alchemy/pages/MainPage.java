package alchemy.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class MainPage {

    private final AndroidDriver driver;

    public MainPage(AndroidDriver driver) {
        this.driver = driver;
    }
    //Время на подгрузку приложения
    public void clickPlay() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

        WebElement playButton = wait.until(driver -> {
            try {
                WebElement el = driver.findElement(
                        AppiumBy.androidUIAutomator(
                                "new UiSelector().className(\"android.widget.Button\").instance(3)")
                );

                return (el.isDisplayed() && el.isEnabled()) ? el : null;

            } catch (Exception e) {
                return null;
            }
        });

        playButton.click();
    }
    //Меню с подсказками
    public void openHintsMenu() {
        // 1️⃣ Ждём кнопку (до 3 сек)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

        WebElement hintsButton = wait.until(driver -> {
            try {
                WebElement el = driver.findElement(
                        AppiumBy.androidUIAutomator(
                                "new UiSelector().className(\"android.widget.Button\").instance(0)")
                );

                return (el.isDisplayed() && el.isEnabled()) ? el : null;

            } catch (Exception e) {
                return null;
            }
        });

        hintsButton.click();

        //Ждём, пока счётчик станет = 2
        long startTime = System.currentTimeMillis();
        long timeout = 30000; // 10 сек

        while (System.currentTimeMillis() - startTime < timeout) {
            try {
                int count = getHintsCount();

                if (count == 2) {
                    System.out.println("Количество подсказок = 2");
                    return;
                }

            } catch (Exception ignored) {}

            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}
        }

        throw new RuntimeException("Счётчик не равен 2, данные не очиищены");
    }

    public static final By WATCH_PARENT = AppiumBy.xpath("//android.widget.Button/..");
    //Не получается явно захватить кнопку через текст или isEnabled, поэтому через перебор с проверкой не исчезла ли кнопка
    public void watchAdForHints() {
        System.out.println("Тап на кнопку Смотреть");

        long startTime = System.currentTimeMillis();
        long timeout = 30000; // 30 секунд таймаут
        boolean clicked = false;

        while ((System.currentTimeMillis() - startTime) < timeout) {
            try {
                WebElement parent = driver.findElement(WATCH_PARENT);
                WebElement button = parent.findElement(AppiumBy.className("android.widget.Button"));

                if (button.isDisplayed() && button.isEnabled()) {
                    parent.click();
                    clicked = true;

                    // ждём секунду и проверяем, пропала ли кнопка
                    Thread.sleep(1000);

                    try {
                        if (!button.isDisplayed()) {
                            System.out.println("BUTTON DISAPPEARED AFTER CLICK, STOPPING LOOP");
                            break; // кнопка пропала — реклама стартовала
                        }
                    } catch (StaleElementReferenceException e) {
                        System.out.println("BUTTON GONE AFTER CLICK (stale), STOPPING LOOP");
                        break; // кнопка уже исчезла, безопасно прерываем цикл
                    }
                }
            } catch (NoSuchElementException | StaleElementReferenceException ignored) {
                //кнопка ещё не появилась, продолжаем ждать
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }

        if (!clicked) {
            throw new RuntimeException("Кнопка Смотреть так и не появилась за 30 секунд");
        }

        //ждём для стабильности перед пропуском рекламы
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {
        }
        
        skipAds();
    }
    //В рекламах нет локаторов за какие можно зацепиться, поймал тех кто выделен явно и собрал признаки для
    //Нахождения кнопки Skip
    public void skipAds() {
        System.out.println("Фиксирую рекламу");

        long startTime = System.currentTimeMillis();
        long timeout = 110000; // 110 сек максимум, т.к. есть рекламы на 90+ секунд

        while (System.currentTimeMillis() - startTime < timeout) {

            try {

                List<WebElement> hintsText = driver.findElements(
                        AppiumBy.androidUIAutomator(
                                "new UiSelector().textMatches(\"(?i).*(your hints|твои подсказки).*\")"
                        )
                );
                //Хотел зацепиться за счётчик, но за постоянный текст на форме оказалось
                if (!hintsText.isEmpty()) {
                    System.out.println("Реклама закончилась");
                    break;
                }

                List<WebElement> candidates = new ArrayList<>();

                // точные кнопки
                candidates.addAll(driver.findElements(
                        AppiumBy.id("com.ilyin.alchemy:id/bigo_ad_btn_close")));
                candidates.addAll(driver.findElements(
                        AppiumBy.id("com.ilyin.alchemy:id/mbridge_windwv_close")));
                candidates.addAll(driver.findElements(
                        AppiumBy.accessibilityId("closeButton")));

                List<WebElement> images = driver.findElements(
                        AppiumBy.className("android.widget.ImageView"));

                for (WebElement el : images) {
                    try {
                        if (el.isDisplayed()
                                && el.isEnabled()
                                && Boolean.parseBoolean(el.getAttribute("clickable"))) {

                            candidates.add(el);
                        }
                    } catch (Exception ignored) {
                    }
                }

                WebElement best = null;
                int bestArea = 0;

                for (WebElement el : candidates) {
                    try {
                        if (!el.isDisplayed()) continue;

                        int area = el.getSize().getWidth() * el.getSize().getHeight();

                        if (area > bestArea) {
                            bestArea = area;
                            best = el;
                        }
                    } catch (Exception ignored) {
                    }
                }


                if (best != null) {

                    try {
                        best.click();
                    } catch (Exception e) {
                    }

                    // даём рекламе обновиться
                    Thread.sleep(1000);

                } else {
                }

            } catch (Exception ignored) {
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public int getHintsCount() {
        long startTime = System.currentTimeMillis();
        long timeout = 15000; // ждём до 15 секунд

        while (System.currentTimeMillis() - startTime < timeout) {
            try {
                List<WebElement> elements = driver.findElements(
                        AppiumBy.className("android.widget.TextView")
                );

                for (WebElement el : elements) {
                    try {
                        if (!el.isDisplayed()) continue;

                        String text = el.getText();

                        if (text != null && text.matches("\\d+")) {
                            return Integer.parseInt(text);
                        }

                    } catch (Exception ignored) {
                    }
                }

            } catch (Exception ignored) {
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }

        throw new RuntimeException("Счётчик подсказок не найден за 15 секунд");
    }
}
