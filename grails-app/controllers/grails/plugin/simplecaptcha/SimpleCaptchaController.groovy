package grails.plugin.simplecaptcha

import org.apache.commons.lang.RandomStringUtils
import org.springframework.context.MessageSource
import org.springframework.web.servlet.LocaleResolver

import javax.imageio.ImageIO
import javax.servlet.http.Cookie
import java.awt.*
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage

class SimpleCaptchaController {

    def simpleCaptchaService
    def grailsApplication
    MessageSource messageSource
    LocaleResolver localeResolver

    private static final DEFAULT_CAPTCHA_CHARS = ('A'..'Z').step(1).join()
    private static final DEFAULT_FONT = 'Serif'

    private static final ONE_DAY_IN_SECONDS = 60 * 60 * 24

    /**
     * If there's already a CAPTCHA in the session, re-use it, otherwise generate a new one. This allows us to show
     * the same CAPTCHA in several places on the same page (which is not possible with ReCaptcha).
     */
    def captcha() {

        // the browser should never cache the CAPTCHA
        // https://github.com/domurtag/grails-simple-captcha/issues/1
        cache false

        def captcha = session[SimpleCaptchaService.CAPTCHA_IMAGE_ATTR] ?: newCaptcha()
        ImageIO.write(captcha, "PNG", response.outputStream)
    }

    private static Graphics2D createGraphic(BufferedImage image, Font font) {
        Graphics2D g2d = image.createGraphics()
        g2d.font = font
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d
    }

    /**
     * Look for a locale-specific charset in resource bundles. If one is not found return the global charset
     * @return the charset from which
     */
    private String getCaptchaCharset() {

        String globalCharset = grailsApplication.config?.simpleCaptcha?.chars ?: DEFAULT_CAPTCHA_CHARS
        Locale locale = localeResolver.resolveLocale(request)
        messageSource.getMessage('simpleCaptcha.chars', null, globalCharset, locale)
    }

    /**
     * Creates a new CAPTCHA, and stores the CAPTCHA image and its solution in the session
     * @return The CAPTCHA image
     */
    private newCaptcha() {
        response.contentType = "image/png"
        response.setHeader("Cache-control", "no-cache")

        String captchaCharset = getCaptchaCharset()
        String fontName = grailsApplication.config?.simpleCaptcha?.font ?: DEFAULT_FONT
        
        final int height = getParamValue(200, 'height')
        final int width = getParamValue(200, 'width')
        final int fontSize = getParamValue(24, 'fontSize')
        final int captchaLength = getParamValue(6, 'length')
        final int bottomPadding = getParamValue(16, 'bottomPadding')
        final int lineSpacing = getParamValue(10, 'lineSpacing')

        // Generate the CAPTCHA solution
        String captchaSolution = RandomStringUtils.random(captchaLength, captchaCharset.toCharArray())

        System.setProperty("java.awt.headless", "true")
        BufferedImage captchaImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        Font font = new Font(fontName, Font.BOLD, fontSize)

        Graphics2D g2d = createGraphic(captchaImage, font)
        Rectangle2D fontRect = font.getStringBounds(captchaSolution, g2d.fontRenderContext)

        // Create a graphic "space" pixels wider and taller than the the font
        captchaImage = new BufferedImage((int) fontRect.width + bottomPadding, (int) fontRect.height + bottomPadding,
                BufferedImage.TYPE_INT_RGB)
        g2d = createGraphic(captchaImage, font)

        // Draw the background
        g2d.color = Color.WHITE
        g2d.fillRect(0, 0, width, height)

        // Draw the lines
        g2d.color = Color.GRAY

        int y1 = lineSpacing
        int x2 = lineSpacing

        int x1 = 0
        int y2 = 0

        while (x1 < width || x2 < width || y1 < height || y2 < height) {
            g2d.drawLine(x1, y1, x2, y2)
            if (y1 < height) {
                x1 = 0
                y1 += lineSpacing
            } else if (x1 < width) {
                y1 = height
                x1 += lineSpacing
            } else {
                x1 = width
                y1 = height
            }

            if (x2 < width) {
                y2 = 0
                x2 += lineSpacing
            } else if (y2 < height) {
                x2 = width
                y2 += lineSpacing
            } else {
                y2 = height
                x2 = width
            }
        }

        // Draw the String
        g2d.color = Color.BLACK
        g2d.drawString(captchaSolution, (int) (bottomPadding / 2), (int) (bottomPadding / 4) + (int) fontRect.height)

        if (simpleCaptchaService.storeInSession()) {
            //	Use the session to store the solution as well as the image itself
            def session = request.getSession(true)
            session.setAttribute(SimpleCaptchaService.CAPTCHA_SOLUTION_ATTR, captchaSolution)
            session.setAttribute(SimpleCaptchaService.CAPTCHA_IMAGE_ATTR, captchaImage)
        } else {
            //	Store the hashed solution in a cookie... this does not currently store the image
            def cookieValue = simpleCaptchaService.encode(captchaSolution).encodeAsURL()
            Cookie cookie = new Cookie(SimpleCaptchaService.CAPTCHA_SOLUTION_ATTR, cookieValue)
            cookie.maxAge = ONE_DAY_IN_SECONDS
			cookie.path = '/'
			response.addCookie(cookie)
        }
        captchaImage
    }

    private getParamValue(Integer defaultValue, String configKey) {
        def configFileValue = grailsApplication.config.simpleCaptcha?."$configKey"
        configFileValue ? configFileValue.toInteger() : defaultValue
    }
}
