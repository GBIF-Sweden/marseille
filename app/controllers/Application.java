package controllers;

import java.util.*;

import org.mindrot.jbcrypt.BCrypt;

import play.*;      // may be useless...
import play.mvc.*;
import play.data.*;
import static play.data.Form.*;

import models.*; 
import views.html.*;  

public class Application extends Controller {


    public static class Login {
        
        public String email;
        public String password;
        
        public String validate() {
            if(User.authenticate(email, password)==null) {
                return "Invalid user or password :-(";
            }
            return null;
        }
        
    }

    /**
     * Login page.
     */
    public static Result login() {
        return ok(
            login.render(form(Login.class))
        );
    }


    /**
     * Handle login form submission.
     */
    public static Result authenticate() {
        Form<Login> loginForm = form(Login.class).bindFromRequest();
        if(loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            // grab the user id for session storage
            
            User usr = User.findByEmail(loginForm.get().email);
            session("userid", Long.toString(usr.id));
            session("email", loginForm.get().email);

            return redirect(
                routes.Datasets.datasets(0,"name","asc","")
            );
        }
    }

    /**
     * Logout and clean the session.
     */
    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(
            routes.Application.login()
        );
    }
  
}