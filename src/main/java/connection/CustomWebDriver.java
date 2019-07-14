package connection;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import util.PropertiesReaderSingleton;

import java.time.Duration;
import java.util.function.Function;

public class CustomWebDriver {

    private WebDriverWait webDriverWait;
    private AppiumDriver<MobileElement> driver;

    public CustomWebDriver(AppiumDriver<MobileElement> driver){
        this.driver = driver;
        resetDriver();
    }

    /**
     * Reset the timeout for 5 seconds
     */
    private void resetTimeout() {
        webDriverWait.withTimeout(Duration.ofSeconds(PropertiesReaderSingleton.getInstance().getPropertyAsInteger("ExplicitWait")));
    }

    /**
     * Get timeOutDuration (5000) from settings.properties file
     */
    public int timeOutDuration() {
        return PropertiesReaderSingleton.getInstance().getPropertyAsInteger("timeOutDuration");
    }

    /**
     * Initiate webDriverWait with timeout of 5 seconds
     */
    private  void resetDriver() {
        Long timeOut = PropertiesReaderSingleton.getInstance().getPropertyAsLong("ExplicitWait");
        webDriverWait = new WebDriverWait(driver, timeOut);
    }

    /**
     * execute 'until' command with default polling (500 mil) and default waitTimeout (5 sec)
     * @param predicate pass to ExpectedCondition (predicate to execute)
     */
    public void until(java.util.function.Function<? super WebDriver, ? extends Object> predicate) {
        webDriverWait.until(predicate);
    }

    /**
     * execute 'until' command with custom requested wait time
     * after the execution , initial the web driver back to default time for polling (500 mil)
     * doesn't initial wait time
     * @param duration is the requested waitTimeOut duration time in milliseconds
     * @param predicate pass to ExpectedCondition (predicate to execute)
     */
        public void withTimeout(long duration, java.util.function.Function<? super WebDriver, ? extends Object> predicate) {
        webDriverWait.withTimeout(Duration.ofMillis(duration)).until(predicate);
        resetTimeout();
    }


    private void resetPolling() {
        webDriverWait.pollingEvery(Duration.ofMillis(500));
    }

    /**
     * execute 'until' command with custom requested polling time
     * after the execution , initial web driver back to default time for wait (5 sec)
     * doesn't initial polling interval
     * @param duration is the requested polling duration time in milliseconds
     * @param predicate pass to ExpectedCondition (predicate to execute)
     */
    public void pollingEvery(long duration, java.util.function.Function<? super WebDriver, ? extends Object> predicate) {
        webDriverWait.pollingEvery(Duration.ofMillis(duration)).until(predicate);
        resetPolling();
    }


    /**
     * execute 'until' command with custom requested polling & wait time
     * and then reset the web driverWait instance
     * @param pollingDuration The timeout duration for polling in milliseconds
     * @param timeOutDuration The timeout duration for timeOut. in milliseconds
     * @param predicate is the predicate to execute
     */
    public void customWaitTimeOutPolling(long pollingDuration, long timeOutDuration, Function<? super WebDriver, ? extends Object> predicate) {
        webDriverWait.pollingEvery(Duration.ofMillis(pollingDuration)).
                withTimeout(Duration.ofMillis(timeOutDuration)).
                until(predicate);
        resetDriver();
    }


    public void ignoreException(long timeOut, String idLocator){
        new WebDriverWait(driver, timeOut).ignoring(StaleElementReferenceException.class).
                until(driver -> driver.findElement(By.id(idLocator)).isDisplayed());
    }
}
