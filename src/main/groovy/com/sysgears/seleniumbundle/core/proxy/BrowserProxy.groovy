package com.sysgears.seleniumbundle.core.proxy

import net.lightbody.bmp.BrowserMobProxyServer
import net.lightbody.bmp.client.ClientUtil
import net.lightbody.bmp.core.har.Har
import net.lightbody.bmp.core.har.HarEntry
import net.lightbody.bmp.proxy.CaptureType
import org.openqa.selenium.Proxy

/**
 * Initializes, configures proxy for browser. Provides methods to interact with proxy and HAR files.
 * HAR file - is HTTP Archive which has all the detailed information about all HTTP requests and their responses.
 */
class BrowserProxy {

    /**
     * Instance of BrowserMobProxyServer.
     */
    private BrowserMobProxyServer proxyServer

    /**
     * Instance of Selenium Proxy.
     */
    private Proxy seleniumProxy

    /**
     * Creates an instance of BrowserProxy and initializes proxy server.
     *
     * @throws IllegalStateException if you provide already running proxy
     */
    BrowserProxy(BrowserMobProxyServer proxyServer) throws IllegalStateException {
        this.proxyServer = startProxyServer(proxyServer)
        this.seleniumProxy = configureSeleniumProxy(ClientUtil.createSeleniumProxy(proxyServer))
    }

    /**
     * Creates new HAR file to log HTTP requests and responses.
     *
     * @return current HAR file if exists, else returns null
     */
    Har createNewHar() {
        proxyServer.newHar()
    }

    /**
     * Gets list of entries from current HAR object.
     *
     * @return list of entries from Har object
     */
    List<HarEntry> getHarEntries() {
        proxyServer.getHar().log.entries
    }

    /**
     * Gets current initialized and configured instance of BrowserMobProxyServer.
     *
     * @return instance of BrowserMobProxy
     */
    BrowserMobProxyServer getProxyServer() {
        proxyServer
    }

    /**
     * Gets current initialized and configured instance of Selenium Proxy.
     *
     * @return instance of Selenium Proxy
     */
    Proxy getSeleniumProxy() {
        seleniumProxy
    }

    /**
     * Configures and starts given instance of BrowserMobProxy.
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
