/**
 * 
 */
package controllers;

import java.util.*;

import play.*;					// may be useless...
import play.mvc.*;
import play.data.*;
import static play.data.Form.*;

import models.*;
import views.html.*;

/**
 * @author korbinus
 *
 */
public class Organizations extends Controller {

//	static Form<Organization> organizationForm = form(Organization.class);
	
	/**
	 * Redirect to the organization list 
	 * @return
	 */
	public static Result index() {
		return redirect(routes.Organizations.organizations(0,"name","asc",""));
	}
	
	public static Result organizations(int page, String sortBy, 
										String order, String filter) {
		return ok(
				listOrganizations.render(
						Organization.page(page, 10, sortBy, order,filter),
						sortBy, order,filter,
						Secured.isAdmin()
						)
				);
	}
	
	/**
	 * Display the 'new organization form'
	 */
	@Security.Authenticated(Secured.class)
	public static Result newOrganization() {
		// security: checked the logged in user has the right to create an organization
		// otherwise redirect to list of organizations
		if(Secured.isAdmin()) {
			Form<Organization> OrganizationForm = form(Organization.class);
			return ok(organizationForm.render(null, OrganizationForm, null, true));
		} else {
			return redirect(routes.Organizations.organizations(0, "name", "asc", ""));
		}
	}
	
	/**
	 * Handle the 'new organization form' submission
	 */
	@Security.Authenticated(Secured.class)
	public static Result saveOrganization() {
		// security: checked the logged in user has the right to create a organization
		// otherwise redirect to list of organizations
		if(Secured.isAdmin()) {		
			try {
				Form<Organization> filledForm = form(Organization.class).bindFromRequest();
				if(filledForm.hasErrors()) {
					return badRequest(organizationForm.render(null, filledForm, null, true));
				} else {
					filledForm.get().save();
					return redirect(routes.Organizations.organizations(0, "name", "asc", ""));
				}
			} catch (Exception e) {
				// correct flash message
				flash("error", "Error with the database");
				return redirect(routes.Organizations.organizations(0,"name","asc",""));			
			}
		} else {
			return redirect(routes.Organizations.organizations(0,"name","asc",""));
		}
	}

	/**
	 * Show the organization
	 */
	public static Result showOrganization(Long id) {
		try {
			return ok(showOrganization.render(Organization.find.byId(id), Secured.isAdmin()));
		} catch (Exception e) {
			flash("error", "Error with the database");
			return redirect(routes.Organizations.organizations(0,"name","asc",""));
		}
	}
	
	/**
	 * Display the 'edit form' of an existing organization
	 * @param id Id of the organization to edit
	 */
	@Security.Authenticated(Secured.class)
	public static Result editOrganization(Long id) {
		// security: checked the logged in user has the right to update the organization
		// otherwise redirect to list of organizations
		if(Secured.isAdmin()){
			try {
				Organization organization = Organization.find.byId(id);
				Form<Organization> filledForm = form(Organization.class).fill(
						organization);
				return ok(organizationForm.render(id, filledForm, organization, true));
			} catch (Exception e) {
				// correct flash message
				flash("error", "Error with the database");
				return redirect(routes.Organizations.organizations(0,"name","asc",""));			
			}
		} else {
			return redirect(routes.Organizations.organizations(0,"name","asc",""));
		}
	}
	
	/**
	 * Handle the 'edit form' submission
	 * @param id Id of the organization to edit
	 */
	@Security.Authenticated(Secured.class)
	public static Result updateOrganization(Long id) {
		// security: checked the logged in user has the right to update the data
		// otherwise redirect to list of datasets
		if(Secured.isAdmin()) {
			try {
				Form<Organization> filledForm = form(Organization.class).bindFromRequest();
				if(filledForm.hasErrors()){
					Organization organization = Organization.find.byId(id);
					return badRequest(organizationForm.render(id, filledForm, organization, true));
				} else {
					filledForm.get().update(id);
					flash("success", "Organization " + filledForm.get().name + "(" + id
							+ ") has been updated");
					return redirect(routes.Organizations.organizations(0,"name","asc",""));	
				}
			} catch (Exception e) {
				// correct flash message
				flash("error", "Error with the database");
				return redirect(routes.Organizations.organizations(0,"name","asc",""));				
			}
		} else {
			return redirect(routes.Organizations.organizations(0,"name","asc",""));
		}
	}
	
	/**
	 * Delete an organization record
	 * @param id Id of the organization to delete
	 */
	@Security.Authenticated(Secured.class)
	public static Result deleteOrganization(Long id) {
		// security: checked the logged in user has the right to delete the organization
		// otherwise redirect to list of organizations
		if(Secured.isAdmin()) {
			Organization.delete(id);
			return redirect(routes.Organizations.organizations(0,"name","asc",""));
		} else {
			return redirect(routes.Organizations.organizations(0,"name","asc",""));
		}
	}
}
