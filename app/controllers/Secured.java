package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.*;

import models.*;

public class Secured extends Security.Authenticator {
    
    @Override
    public String getUsername(Context ctx) {
        return ctx.session().get("email");
    }
    
    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.Application.login());
    }
    
    // Access rights
    public static boolean isAdmin() {
    	Logger.info("Current username: " + Context.current().session().get("email"));
//    	Logger.info("Current username: " + Context.current().request().username());
    	// Note: that's the email address, not the username that we send below...
//    	return User.isAdmin(Context.current().request().username());
    	// @todo: check this is more secure that request().username()
    	return User.isAdmin(Context.current().session().get("email"));
    }
}