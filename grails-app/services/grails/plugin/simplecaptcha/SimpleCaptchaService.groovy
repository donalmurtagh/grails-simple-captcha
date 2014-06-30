package grails.plugin.simplecaptcha

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.codehaus.groovy.grails.web.util.WebUtils

import java.security.MessageDigest
import javax.servlet.http.Cookie

class SimpleCaptchaService {

    static final CAPTCHA_SOLUTION_ATTR = 'captcha'
    static final CAPTCHA_IMAGE_ATTR = 'captchaImage'

    static transactional = false

    GrailsApplication grailsApplication

    /**
     * Indicates if the CAPTCHA was solved correctly
     * @param captchaSolution The CAPTCHA solution provided by the user
     * @return Indicates whether the CAPTCHA challenge was passed
     */
    boolean validateCaptcha(String captchaSolution) {

        GrailsWebRequest webRequest = WebUtils.retrieveGrailsWebRequest()

        if (storeInSession()) {
            //	extract the value from the session
            def session = webRequest.session
            String solution = session[CAPTCHA_SOLUTION_ATTR]

            // remove the CAPTCHA so a new one will be generated next time one is requested
            session.removeAttribute(CAPTCHA_SOLUTION_ATTR)
            session.removeAttribute(CAPTCHA_IMAGE_ATTR)

            captchaSolution ? solution?.compareToIgnoreCase(captchaSolution) == 0 : false

        } else {
            //	We'll use the cookie instead
            def request = webRequest.currentRequest
            String solution = readCookie(CAPTCHA_SOLUTION_ATTR, request)

            def response = webRequest.currentResponse

            //	Remove the cookie if we have a response
            if (response) {
                Cookie solutionCookie = new Cookie(CAPTCHA_SOLUTION_ATTR, null)
                solutionCookie.maxAge = 0
                response.addCookie(solutionCookie)
            }

            captchaSolution ? solution == encode(captchaSolution) : false
        }
    }

    /**
     * @return Indicates if session storage is being used
     */
    boolean storeInSession() {
        def pluginConfig = grailsApplication.config.simpleCaptcha

        // Not sure why an explicit null check is necessary, it was submitted as a bug-fix by a user
        if (pluginConfig.storeInSession != null) {
            return pluginConfig.storeInSession.asBoolean()
        }
        true
    }

    /**
     * Creates an MD5 digest version of a lower-case version of a string
     * @param stringToEncode
     * @param The encoded string
     */
    String encode(String stringToEncode) {
        def digest = MessageDigest.getInstance("MD5")
        new BigInteger(1, digest.digest(stringToEncode.toLowerCase().bytes)).toString(16).padLeft(32, "0")
    }

    /**
     * Reads a cookie
     * @param name The name used to store the cookie
     * @return The value of the cookie, or null if no such value exists
     */
    private String readCookie(String name, request) {

        //	Get all the cookies
        def cookie = request.cookies?.find {it.name == name}
        cookie ? cookie.value?.decodeURL() : null
    }
}
