package com.sysgears.seleniumbundle.core.proxy

import net.lightbody.bmp.BrowserMobProxyServer
import net.lightbody.bmp.client.ClientUtil
import net.lightbody.bmp.core.har.Har
import net.lightbody.bmp.core.har.HarEntry
import net.lightbody.bmp.core.har.HarResponse
import net.lightbody.bmp.proxy.CaptureType
import org.json.JSONObject
import org.openqa.selenium.Proxy

/**
 * Initializes and configures the proxy for a browser. Proxy is set between a browser and a server. The proxy is used
 * for capturing network traffic sent and received by specific domain for further analysis. The packets are stored in
 * HAR files.
 *
 * HAR file - is HTTP Archive which has detailed information about HTTP requests and responses.
 */
class BrowserProxy {

    /**
     * Instance of Selenium Proxy.
     */
    Proxy seleniumProxy

    /**
     * Instance of BrowserMobProxyServer.
     */
    private BrowserMobProxyServer proxyServer

    /**
     * Creates an instance of BrowserProxy and starts the proxy server.
     *
     * @throws IllegalStateException if you provide already running proxy
     */
    BrowserProxy(BrowserMobProxyServer proxyServer) throws IllegalStateException {
        this.proxyServer = startProxyServer(proxyServer)
        this.seleniumProxy = configureSeleniumProxy(ClientUtil.createSeleniumProxy(proxyServer))
    }

    /**
     * Creates a new HAR file to log HTTP requests and responses.
     *
     * @return existing HAR file, or null if none exists or HAR capture was disabled
     */
    Har createNewHar() {
        proxyServer.newHar()
    }

    /**
     * Gets list of entries from the current HAR object.
     *
     * @return list of entries from Har object
     */
    List<HarEntry> getHarEntries() {
        proxyServer.getHar().log.entries
    }

    /**
     * Gets List of HarEntries of the requests which were sent to the given url by the given method.
     *
     * @param url which the request was sent to
     * @param method which the request was sent by e.g. GET, POST, etc.
     *
     * @return list of found HarEntries
     */
    List<HarEntry> findHarEntriesBy(String url, String method = null) {
        getHarEntries().findAll {
            (method ? it.request.method == method : true) && it.request.url == url
        }
    }

    /**
     * Gets List of HarEntries of the requests which were sent to the given url by the given method with the given query
     * parameters.
     *
     * @param url which the request was sent to
     * @param queryParameters map of parameters
     * @param method which the request was sent by e.g. GET, POST, etc.
     *
     * @return list of found HarEntries
     */
    List<HarEntry> findHarEntriesByQueryString(String url, Map queryParameters, String method = null) {
        findHarEntriesBy(url, method).findAll {
            it.request.queryString.collectEntries {
                [it.getName(), it.getValue()]
            }.entrySet().contains(queryParameters.entrySet())
        }
    }

    /**
     * Gets List of HarEntries of the requests which were sent to the given url by the given method with the given
     * body parameters.
     *
     * @param url which the request was sent to
     * @param requestBody key-value pairs represented in map
     * @param method which the request was sent by e.g. GET, POST, etc.
     *
     * @return list of found HarEntries
     */
    List<HarEntry> findHarEntriesByBody(String url, Map requestBody, String method = null) {
        findHarEntriesBy(url, method).findAll {
            new JSONObject(it.request.postData.text).toMap().entrySet().containsAll(requestBody.entrySet())
        }
    }

    /**
     * Gets HarEntry of the last request which was sent to the given url by the given method.
     *
     * @param url which the request was sent to
     * @param method which the request was sent by e.g. GET, POST, etc.
     *
     * @return last HarEntry from the found list
     */
    HarEntry findLastHarEntryBy(String url, String method = null) {
        findHarEntriesBy(url, method).last()
    }

    /**
     * Gets HarResponse of the last request which was sent to the given url by the given method.
     *
     * @param url which the request was sent to
     * @param method which the request was sent by e.g. GET, POST, etc.
     *
     * @return HarResponse of the found HTTP request
     */
    HarResponse findLastResponseBy(String url, String method = null) {
        findLastHarEntryBy(url, method).response
    }

    /**
     * Configures and starts the given instance of BrowserMobProxy.
     *
     * @param server instance of BrowserMobProxy
     *
     * @return instance of BrowserMobProxy
     *
     * @throws IllegalStateException if you try to start already running server
     */
    private BrowserMobProxyServer startProxyServer(BrowserMobProxyServer server) throws IllegalStateException {
        server.setTrustAllServers(true)
        server.start()
        server.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT)
        server
    }

    /**
     * Configures Selenium Proxy to accept SSL certificates.
     *
     * @param proxy instance of Selenium Proxy
     *
     * @return configured instance of Selenium Proxy
     */
    private Proxy configureSeleniumProxy(Proxy proxy) {
        proxy.setProxyType(Proxy.ProxyType.MANUAL)
        String proxyStr = String.format("%s:%d", InetAddress.getLocalHost().getCanonicalHostName(), proxyServer.getPort())
        proxy.setHttpProxy(proxyStr)
        proxy.setSslProxy(proxyStr)
    }
}
