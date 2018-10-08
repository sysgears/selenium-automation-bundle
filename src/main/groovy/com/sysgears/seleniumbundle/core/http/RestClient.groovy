package com.sysgears.seleniumbundle.core.http

import com.mashape.unirest.http.Unirest
import com.mashape.unirest.http.exceptions.UnirestException
import groovy.util.logging.Slf4j
import org.apache.http.HttpException

/**
 * Provides the methods to send HTTP requests.
 */
@Slf4j
class RestClient {

    /**
     * Username for authorization.
     */
    private String username

    /**
     * Password for authorization.
     */
    private String password

    /**
     * Creates an instance of RestClient.
     *
     * @param username used for basic authentication
     * @param password used for basic authentication
     */
    RestClient(String username, String password) {
        this.username = username
        this.password = password
    }

    /**
     * Sends the GET request on the given URL with the specified authorization credentials.
     *
     * @param url URL of the API
     *
     * @return response as a map
     *
     * @throws HttpException if any HTTP errors occurred or if the request status is not "200"
     */
    Map get(String url) throws HttpException {
        try {
            def response = Unirest.get(url).basicAuth(username, password).asJson()

            if (response.status != 200) {
                log.error("Wrong status of the GET request to $url, status code: ${response.status}, response body: " +
                        "${response.body}")
                throw new HttpException("Wrong status of the GET request to $url, status code: ${response.status}, " +
                        "response body: ${response.body}")
            }

            response.body.object.toMap()
        } catch (UnirestException e) {
            log.error("Error in the GET request to $url has occurred", e)
            throw new HttpException("Error in the GET request to $url has occurred", e)
        }
    }

    /**
     * Sends the DELETE request on the given URL with the specified authorization credentials.
     *
     * @param url URL of the API
     *
     * @throws HttpException if any HTTP errors occurred or the request status is not "204"
     */
    void delete(String url) throws HttpException {
        try {
            def response = Unirest.delete(url).basicAuth(username, password).asJson()

            if (response.status != 204) {
                log.error("Wrong status of the DELETE request to $url, status code: ${response.status}, response " +
                        "body: ${response.body}")
                throw new HttpException("Wrong status of the DELETE request to $url, status code: ${response.status}," +
                        " response body: ${response.body}")
            }
        } catch (UnirestException e) {
            log.error("Error in the DELETE request to $url has occurred", e)
            throw new HttpException("Error in the DELETE request to $url has occurred", e)
        }
    }
}
