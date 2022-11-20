package org.voyager;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v101.fetch.Fetch;
import org.openqa.selenium.devtools.v101.fetch.model.HeaderEntry;
import org.openqa.selenium.devtools.v101.network.Network;

import org.voyager.exception.MockerException;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

/**
 * Created By vigneshwaranthangavelu  @ 14/11/22 - 12:01 am
 */

public class MockerUtils {


    /**
     * Mock service.
     *
     * @param chromeDriver
     * @param url     the end point to mock
     * @param mockedResponseBody the mocked response body (Modified Response Body)
     * @implNote - Using CDP devtool fetch the request has been mocked.
     * @see  <a href="https://chromedevtools.github.io/devtools-protocol/tot/Fetch/">chromedevtools Fetch</a>
     */
    public void mockService(ChromeDriver chromeDriver,String url, String mockedResponseBody) throws MockerException {
        try {
            mockService(chromeDriver,url,null,mockedResponseBody,200);
        } catch (Throwable e) {
            throw new MockerException(e);
        }
    }

    /**
     * Mock service.
     *
     * @param chromeDriver
     * @param url     the end point to mock
     * @param headerEntries list of headers
     * @param mockedResponseBody the mocked response body (Modified Response Body)
     * @param responseCode HTTP response code
     * @implNote - Using CDP devtool fetch the request has been mocked.
     * @see <a href="https://chromedevtools.github.io/devtools-protocol/tot/Fetch/">chromedevtools Fetch</a>
     */
    public void mockService(ChromeDriver chromeDriver, String url, List<HeaderEntry> headerEntries, String mockedResponseBody, Integer responseCode) throws MockerException {
        try {
            String mockedEncodedResponse = Base64.getEncoder().encodeToString(mockedResponseBody.getBytes());
            DevTools chromeDevTools;

            chromeDevTools = chromeDriver.getDevTools();
            chromeDevTools.createSession();

            chromeDevTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
            chromeDevTools.send(Fetch.enable(Optional.empty(), Optional.empty()));

            chromeDevTools.addListener(Fetch.requestPaused(), requestPaused -> {
                String currentUrl = requestPaused.getRequest().getUrl();
                if (currentUrl.equalsIgnoreCase(url)) {
                    chromeDevTools.send(Fetch.fulfillRequest(requestPaused.getRequestId(),
                             responseCode,
                            Optional.ofNullable(headerEntries),
                            Optional.empty(),
                            Optional.of(mockedEncodedResponse),
                            Optional.empty()));
                } else {
                    chromeDevTools.send(Fetch.continueRequest(requestPaused.getRequestId(), Optional.of(currentUrl),
                            Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
                }
            });
            DevToolThread.setChromeDevTools(chromeDevTools);
        } catch (Throwable e) {
            throw new MockerException(e);
        }
    }

    public void disableMocking() throws MockerException {
        try{
            DevTools chromeDevTools = DevToolThread.getChromeDevTools();
            chromeDevTools.send(Network.disable());
            chromeDevTools.send(Fetch.disable());
        }catch(Throwable e){
            throw new MockerException(e);
        }
    }

}
