import org.ratpackframework.session.Session
import org.ratpackframework.session.store.MapSessionsModule
import org.ratpackframework.session.store.SessionStorage

import static groovy.json.JsonOutput.toJson
import static org.ratpackframework.groovy.RatpackScript.ratpack

ratpack {
    modules {
        register(new MapSessionsModule(10, 5))
    }

    handlers {
        prefix("apps") {
            handler {
                if (!get(SessionStorage).auth) {
                    clientError(401)
                    return
                } 
                next()
            }

            post("protect") {
                response.send "application/json", toJson("protected content")
            }
        }

        prefix("auth") {
            post("login") {
                def username = request.form.username
                def password = request.form.password
                if (username == "user" && password == "pass") {
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
        
        handler {
            clientError(404)
        }
    }
}
