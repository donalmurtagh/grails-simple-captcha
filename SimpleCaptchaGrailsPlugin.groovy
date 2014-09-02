class SimpleCaptchaGrailsPlugin {

    // the plugin version
    def version = "1.0.0"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.0 > *"
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

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/simple-captcha"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Location of the plugin's issue tracker.
    def issueManagement = [ system: "GitHub", url: "https://github.com/domurtag/grails-simple-captcha/issues" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://github.com/domurtag/grails-simple-captcha" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
