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
                    response.status(401).send()
                } else {
                    next()
                }
            }

            post("protect") {
                response.send "application/json", toJson("protected content")
            }
        }

        post("auth/login") {
            def username = request.form.username
            def password = request.form.password
            if (username == "user" && password == "pass") {
                get(SessionStorage).auth = true
                response.status(200).send "application/json", toJson("")
            } else {
                response.status(401).send()
            }
        }

        assets "public", "index.html"
    }
}


