package grails.simple.captcha

import grails.plugins.*
class SimpleCaptchaGrailsPlugin extends Plugin {

    // the plugin version
    def version = "1.0.0"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.0.1 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = ["grails-app/conf", "grails-app/domain", "grails-app/i18n", "grails-app/utils",
                          "grails-app/views", "lib", "scripts", "web-app", "grails-app/views/error.gsp"]

    def title = "Simple Captcha Plugin"
    def author = "Donal Murtagh"
    def authorEmail = "domurtag@yahoo.co.uk"
    def description = '''\
Creates simple image CAPTCHAs that protect against automated completion and submission of HTML forms
'''
    def developers = [[name: 'Donal Murtagh', email: 'domurtag@yahoo.co.uk'], [name: 'Rohit Bishnoi', email: 'rbdharnia@gmail.com']]
    def profiles = ['web']

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/simple-captcha"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Location of the plugin's issue tracker.
    def issueManagement = [ system: "GitHub", url: "https://github.com/domurtag/grails-simple-captcha/issues" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://github.com/domurtag/grails-simple-captcha" ]
}
