/**
 * Controller for managing datasets
 * @author korbinus
 */
package controllers;

import java.util.*;

import play.*;					// may be useless...
import play.mvc.*;
import play.data.*;
import static play.data.Form.*;

import models.*;
import views.html.*;

public class Datasets extends Controller {

//	static Form<Dataset> datasetForm = form(Dataset.class);

	/**
	 * Index. Redirect to the dataset list
	 * @return
	 */
	public static Result index() {
		// redirect to the dataset list
		return redirect(routes.Datasets.datasets(0,"name","asc",""));
	}

	/**
	 * Display the list of datasets
	 */
	public static Result datasets(int page, String sortBy, String order, String filter) {
		return ok(
				listDatasets.render(
		                Dataset.page(page, 10, sortBy, order, filter),
		                sortBy, order, filter,
		                Secured.isAdmin()
                )
        );
	}

	/**
	 * Display the 'new dataset form'
	 */
	@Security.Authenticated(Secured.class)
	public static Result newDataset() {
		// security: checked the logged in user has the right to create a dataset
		// otherwise redirect to list of datasets
		if(Secured.isAdmin()) {
			Form<Dataset> DatasetForm = form(Dataset.class);
			return ok(datasetForm.render(null, DatasetForm, null,true));
		} else {
			return redirect(routes.Datasets.datasets(0,"name","asc",""));
		}
	}

	/**
	 * Handle the 'new dataset form' submission
	 */
	@Security.Authenticated(Secured.class)
	public static Result saveDataset() {
		// security: checked the logged in user has the right to create a dataset
		// otherwise redirect to list of datasets
		if(Secured.isAdmin()) {
			Form<Dataset> filledForm = form(Dataset.class).bindFromRequest();
			if (filledForm.hasErrors()) {
				return badRequest(datasetForm.render(null, filledForm, null,true));
			} else {
				filledForm.get().save();
				return redirect(routes.Datasets.datasets(0,"name","asc",""));
			}
		} else {
			return redirect(routes.Datasets.datasets(0,"name","asc",""));
		}
	}

	/**
	 * Show the dataset
	 */
	public static Result showDataset(Long id) {
		try {
			return ok(showDataset.render(Dataset.find.byId(id), Secured.isAdmin()));
		} catch (Exception e) {
			flash("error", "Error with the database " + e.getMessage());
			return redirect(routes.Datasets.datasets(0,"name","asc",""));
		}
	}
	
	/**
	 * Display the 'edit form' of an existing dataset
	 * 
	 * @param id Id of the dataset to edit
	 */
	@Security.Authenticated(Secured.class)
	public static Result editDataset(Long id) {
		// security: checked the logged in user has the right to update the data
		// otherwise redirect to list of datasets
		if(Secured.isAdmin()){
			try{
				Dataset dataset = Dataset.find.byId(id); 
				Form<Dataset> filledForm = form(Dataset.class).fill(
						dataset);
				return ok(datasetForm.render(id, filledForm, dataset,true));
			} catch (Exception e) {
				// correct flash message
				flash("error", "Error with the database");
				return redirect(routes.Datasets.datasets(0,"name","asc",""));
			}
		} else {
			return redirect(routes.Datasets.datasets(0,"name","asc",""));
		}
	}

	/**
	 * Handle the 'edit form' submission
	 * 
	 * @param id Id of the dataset to update
	 */
	@Security.Authenticated(Secured.class)
	public static Result updateDataset(Long id) {
		// security: checked the logged in user has the right to update the data
		// otherwise redirect to list of datasets
		if(Secured.isAdmin()) {
			Form<Dataset> filledForm = form(Dataset.class).bindFromRequest();
			if (filledForm.hasErrors()) {
				Dataset dataset = Dataset.find.byId(id);
				return badRequest(datasetForm.render(id, filledForm, dataset,true));
			} else {
				try {
					filledForm.get().update(id);
					flash("success", "Dataset " + filledForm.get().name + "(" + id
							+ ") has been updated");
					return redirect(routes.Datasets.datasets(0,"name","asc",""));
				} catch (Exception e) {
					flash("error", "Error with the database " + e.getMessage());
					return redirect(routes.Datasets.datasets(0,"name","asc",""));
				}
			}
		} else {
			return redirect(routes.Datasets.datasets(0,"name","asc",""));
		}
	}
	/**
	 * Delete a dataset record
	 * @param id Id of the dataset to delete
	 */
	@Security.Authenticated(Secured.class)
	public static Result deleteDataset(Long id) {
		// security: checked the logged in user has the right to delete the dataset
		// otherwise redirect to list of datasets
		if(Secured.isAdmin()) {
			Dataset.delete(id);
			return redirect(routes.Datasets.datasets(0,"name","asc",""));
		} else {
			return redirect(routes.Datasets.datasets(0,"name","asc",""));
		}
	}

}