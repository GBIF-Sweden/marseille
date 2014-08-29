/**
 * Controller for managing users
 * @author korbinus
 */
package controllers;

import java.util.*;

import play.*;
import play.mvc.*;
import play.mvc.Http.Context;
//import play.api.data.*;
import play.data.*;
import static play.data.Form.*;

import models.*;
import views.html.*;

import org.mindrot.jbcrypt.BCrypt;
// use of newer commons.lang3 instead of commons.lang
// TODO remove the former from lib/ when Play bundles it
import org.apache.commons.lang3.text.WordUtils;

@Security.Authenticated(Secured.class)
public class Users extends Controller {
	
//	static Form<User> userForm = form(User.class);
        static Boolean isSelf = false;          // flag for self logged user
	
	/**
	 * Index. Redirect to the user list
	 * @return
	 */
	public static Result index() {
		//redirect to the user list
		return redirect(routes.Users.users(0,"name","asc",""));
	}
	
	/**
	 * Display the list of users
	 * @todo check if the user has the rights to do so
	 */
	public static Result users(int page, String sortBy, 
								String order, String filter) {
		return ok(
				listUsers.render(
						User.page(page, 10, sortBy, order, filter), 
						sortBy, order, filter,
                                                Secured.isAdmin()
				)
		);
	}
	
	/**
	 * Display the 'new user form'
	 */
	public static Result newUser() {
		if(Secured.isAdmin()) {
			Form<User> UserForm = form(User.class);
//			return ok(createUserForm.render(userForm));
                        return ok(userForm.render(null, UserForm, true, isSelf));
		} else {
			return forbidden();
		}

	}

	/**
	 * Check if the two strings are identical. 
	 * Usage: new password and new email verification.
	 * @param form
	 * @param field
	 * @return true if matches, otherwise false
	 */
	private static boolean confirmValue(Form<User> form, String field) {
		if(form.field(field).value().equals(
				form.field("confirm" + WordUtils.capitalize(field)).value()
		)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Encrypt the received password using the BCrypt encryption.
	 * Note that you can easily increase the complexity by changing its value
	 * @param password
	 * @return String
	 */
	private static String encrypt(String password)
	{
		int complexity = 12;
		
		return BCrypt.hashpw(password, 
				BCrypt.gensalt(complexity));
	}
	
	/**
	 * Save the user created in the 'new user form'
	 * 
	 * @return
	 */
	// TODO send confirmation email 
	public static Result saveUser(){
		if(request().method() != "POST") {
			return newUser();
		}
		if(Secured.isAdmin()){
			Form<User> filledForm = form(User.class).bindFromRequest();
			// Evaluate confirmation data
			// check password confirmation 
			// (inspired from http://www.youtube.com/watch?v=rG6E0O0HkhA 
			// many many thanks!)
			if(!filledForm.field("password").value().isEmpty()){
				if(!confirmValue(filledForm, "password")){
					filledForm.reject("confirmPassword",
										"Passwords don't match");
				}
			} else {
				// Since there's no @Required for password in User.java,
				// we need to enforce this at this level 
				filledForm.reject("password", "Password empty");
			}
			
			if(!filledForm.field("email").value().isEmpty()){
				if(!confirmValue(filledForm, "email")){
					filledForm.reject("confirmEmail", "Emails don't match");
				}
			} 
	
			if(filledForm.hasErrors()){
//				return badRequest(createUserForm.render(filledForm));
                                return badRequest(userForm.render(null, filledForm, true, isSelf));
			} else {
				// if we're here, it's green, we can create the new user
				// get the form data
				User user = filledForm.get();
				
				user.password = encrypt(user.password);
	
				user.save();
	
				flash("success", "User " + filledForm.get().name 
						+ " has been created");				
				return redirect(routes.Users.users(0,"name","asc",""));
			}
		} else {
			return forbidden();
		}
			
	}
	
	/**
	 * Display the 'edit form' of an existing user
	 * 
	 * @param id Id of the user to edit
	 */
	public static Result editUser(Long id) {
		if(Secured.isAdmin()){
			try{
				User user = User.find.byId(id);
//				user.password = null;
                                isSelf = user.email.equals(Context.current().session().get("email"));
				Form<User> filledForm = form(User.class).fill(user);
//				return ok(editUserForm.render(id, filledForm));
                                return ok(userForm.render(id, filledForm, true, isSelf));
			} catch (Exception e) {
				// correct flash message
				flash("error", "Error with the database");
				return redirect(routes.Users.users(0,"name","asc",""));
			}		
		} else {
			return forbidden();
		}
	}

	/**
	 * Handle the 'edit form' submission
	 * 
	 * @param id Id of the user to edit
	 * @todo handle password update
	 */
	public static Result updateUser(Long id) {
		if(request().method() != "POST") {
			return redirect(routes.Users.users(0,"name","asc",""));
		}
		
		Form<User> filledForm = form(User.class).bindFromRequest();
		User currentState = User.find.byId(id);

		// Evaluate confirmation data
		
		// check password confirmation
		if(filledForm.field("password").value() != null 
                            && !filledForm.field("password").value().isEmpty()){
			if(!confirmValue(filledForm, "password")){
				filledForm.reject("confirmPassword",
				"Passwords don't match");
			}
		} 
		
		if(filledForm.field("email").value() != null 
                                && !filledForm.field("email").value().isEmpty()
				&& !filledForm.field("email").value().equals(currentState.email)){
			if(!confirmValue(filledForm, "email")){
				filledForm.reject("confirmEmail", "Emails don't match");
			}
		} 
		
		if (filledForm.hasErrors()) {
			return badRequest(editUserForm.render(id, filledForm));
		} else {
			
			User newState = filledForm.get();

			if(newState.password == null || newState.password.isEmpty()) {
				// if the password is empty (ie user hasn't changed it)
				// get the encrypted old one for later save
				newState.password = currentState.password;
			} else {
				// otherwise generate a new encryption for it
				newState.password = encrypt(newState.password);
			}
			
			newState.update(id);
			flash("success", "User " + filledForm.get().name + "(" + id
					+ ") has been updated");
			return redirect(routes.Users.users(0,"name","asc",""));
		}
	}	
	
	public static Result deleteUser(Long id) {
		if(Secured.isAdmin()){
			User.delete(id);
			return redirect(routes.Users.users(0,"name","asc",""));
		} else {
			return forbidden();
		}
	}
}