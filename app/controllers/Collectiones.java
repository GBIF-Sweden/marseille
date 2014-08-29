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
public class Collectiones extends Controller {

	
	/**
	 * Redirect to the Collectio list 
	 * @return
	 */
	public static Result index() {
		return redirect(routes.Collectiones.collectiones(0,"name","asc",""));
	}
	
	public static Result collectiones(int page, String sortBy, 
										String order, String filter) {
		return ok(
				listCollectiones.render(
						Collectio.page(page, 10, sortBy, order,filter),
						sortBy, order,filter,
						Secured.isAdmin()
						)
				);
	}
	
	/**
	 * Display the 'new Collectio form'
	 */
	@Security.Authenticated(Secured.class)
	public static Result newCollectio() {
		// security: checked the logged in user has the right to create a collection
		// otherwise redirect to list of collectiones
		if(Secured.isAdmin()) {
			Form<Collectio> CollectioForm = form(Collectio.class);
			return ok(collectioForm.render(null, CollectioForm, null,true));
		} else {
			return redirect(routes.Collectiones.collectiones(0,"name","asc",""));
		}
	}

	/**
	 * Handle the 'new Collectio form' submission
	 */
	@Security.Authenticated(Secured.class)
	public static Result saveCollectio() {
		// security: checked the logged in user has the right to create a collectio
		// otherwise redirect to list of collectiones
		if(Secured.isAdmin()) {
			try {
				Form<Collectio> filledForm = form(Collectio.class).bindFromRequest();
				if(filledForm.hasErrors()) {
					return badRequest(collectioForm.render(null, filledForm, null,true));
				} else {
					filledForm.get().save();
					return redirect(routes.Collectiones.collectiones(0, "name", "asc", ""));
				}
			} catch (Exception e) {
				// correct flash message
				flash("error", "Error with the database");
				return redirect(routes.Collectiones.collectiones(0,"name","asc",""));			
			}
		} else {
			return redirect(routes.Collectiones.collectiones(0,"name","asc",""));
		}
	}
	/**
	 * Show the collection
	 */
	public static Result showCollectio(Long id) {
		try {
			return ok(showCollectio.render(Collectio.find.byId(id), Secured.isAdmin()));
		} catch (Exception e) {
			flash("error", "Error with the database");
			return redirect(routes.Collectiones.collectiones(0,"name","asc",""));
		}
	}
	
	/**
	 * Display the 'edit form' of an existing Collectio
	 * @param id Id of the Collectio to edit
	 */
	@Security.Authenticated(Secured.class)
	public static Result editCollectio(Long id) {
		// security: checked the logged in user has the right to update the collectio
		// otherwise redirect to list of collectiones
		if(Secured.isAdmin()){
			try {
				// retrieve the original record for getting creationDate and updateDate
				Collectio collectio = Collectio.find.byId(id); 
				Form<Collectio> filledForm = form(Collectio.class).fill(
						collectio);
				return ok(collectioForm.render(id, filledForm, collectio, true));
			} catch (Exception e) {
				// correct flash message
				flash("error", "Error with the database");
				return redirect(routes.Collectiones.collectiones(0,"name","asc",""));			
			}
		} else {
			return redirect(routes.Collectiones.collectiones(0,"name","asc",""));
		}
	}
	
	/**
	 * Handle the 'edit form' submission
	 * @param id Id of the Collectio to edit
	 */
	@Security.Authenticated(Secured.class)
	public static Result updateCollectio(Long id) {
		// security: checked the logged in user has the right to update the data
		// otherwise redirect to list of datasets
		if(Secured.isAdmin()) {		
			try {
				Form<Collectio> filledForm = form(Collectio.class).bindFromRequest();
				if(filledForm.hasErrors()){
					// retrieve the original record for getting creationDate and updateDate
					Collectio collectio = Collectio.find.byId(id);
					return badRequest(collectioForm.render(id, filledForm, collectio,true));
				} else {
					filledForm.get().update(id);
					flash("success", "Collectio " + filledForm.get().name + "(" + id
							+ ") has been updated");
					return redirect(routes.Collectiones.collectiones(0,"name","asc",""));	
				}
			} catch (Exception e) {
				// correct flash message
				flash("error", "Error with the database");
				return redirect(routes.Collectiones.collectiones(0,"name","asc",""));				
			}
		} else {
			return redirect(routes.Collectiones.collectiones(0,"name","asc",""));
		}
	}
	
	/**
	 * Delete an Collectio record
	 * @param id Id of the Collectio to delete
	 */
	@Security.Authenticated(Secured.class)
	public static Result deleteCollectio(Long id) {
		// security: checked the logged in user has the right to delete the collectio
		// otherwise redirect to list of datasets
		if(Secured.isAdmin()) {
			Collectio.delete(id);
			return redirect(routes.Collectiones.collectiones(0,"name","asc",""));
		} else {
			return redirect(routes.Collectiones.collectiones(0,"name","asc",""));
		}
	}
}
