import groovywebconsole.ReloadingThing
import groovywebconsole.ScriptExecutor
import org.ratpackframework.groovy.templating.TemplateRenderer

import static groovy.json.JsonOutput.toJson
import static org.ratpackframework.groovy.RatpackScript.ratpack

import org.ratpackframework.session.*
import org.ratpackframework.session.store.SessionStorage
import org.ratpackframework.session.store.MapSessionStore
import org.ratpackframework.handling.Exchange
import org.ratpackframework.session.store.MapSessionsModule


ratpack {
	modules {
		register(new MapSessionsModule(10, 5))        
    }
	
    handlers {
		handler {
			  def path = request.path
			  if (path.startsWith("apps")) {
			  def cookie = request.oneCookie("JSESSIONID")
			  def token = get(MapSessionStore).get(cookie).get(cookie)
				if (!token) {
				response.status(401).send("")	
				} else {
				next()
				}				
			  }	else {
			    next()
			  }
		}

		post ("auth/login") {
		    def cookie = request.oneCookie("JSESSIONID")
			def storage = get(MapSessionStore).get(cookie)
			def form = request.getForm()
			def username = form.get("username")
			def password = form.get("password")
			if (username == "user" && password == "pass") {
				storage.putIfAbsent(cookie, true)
				response.status(200).send "application/json", toJson("")
			} else {
			  response.status(401).send("")	
			}			
		}
		
		post ("apps/protect") {
			response.send "application/json", toJson("protected content")
		}

        assets "public", "index.html"
    }
}


