import org.ratpackframework.groovy.bootstrap.RatpackScriptApp
import org.ratpackframework.groovy.templating.TemplatingModule
import org.ratpackframework.session.Session
import org.ratpackframework.session.store.MapSessionsModule
import org.ratpackframework.session.store.SessionStorage

import java.util.logging.Logger
import static groovy.json.JsonOutput.toJson
import static org.ratpackframework.groovy.RatpackScript.ratpack

ratpack {
    def logger = Logger.getLogger("")

    modules {
        // Make templates reloadable if we are in reload mode, otherwise cache them
        def isReloadable = System.getProperty(RatpackScriptApp.Property.RELOADABLE)
        get(TemplatingModule).cacheSize = isReloadable ? 0 : 100

        // Enable non persistent map based session storage
        register(new MapSessionsModule(10, 5))
    }

    handlers {
        handler {
            logger.info("request: " + request.uri)
            next()
        }

        prefix("apps") {
            handler {
                if (!get(SessionStorage).auth) {
                    clientError(401)
                    return
                } 
                next()
            }

            post("protect") {
                response.send "application/json", toJson(request.text)
            }
        }

        prefix("auth") {
            post("login") {
                def form = request.form
                if (form.username == "user" && form.password == "pass") {                    
    				logger.info 'User Logged in successfully.'
                    get(SessionStorage).auth = true
                    response.send "application/json", toJson("")
                } else {
                    clientError(401)
                }
            }
            post("logout") {
                get(Session).terminate()
                response.send()
            }
        }

        assets "public", "index.html"
    }
}
