/**
 * 
 */
package models;

import java.util.*;
import java.util.Map.Entry;

import javax.persistence.*;

import play.db.ebean.*;
import play.db.ebean.Model.Finder;
import play.data.format.*;
import play.data.validation.*;
import play.data.validation.Constraints.*;

import com.avaje.ebean.*;

/**
 * @author korbinus
 *
 * "Collection" is already used by Java so we use the latin Collectio instead.
 */
@Entity
public class Collectio  extends Model{

	@Id
	public Long id;

	@Required
	@Column(unique = true, nullable = false)
	public String code;
	@Required
	@Column(unique = true, nullable = false)
	public String name;

	@Column(unique = true, nullable = true)
	public String bciID;
	
	public String webUrl;
	@Column(columnDefinition = "text")
	public String description;

	public Long numberSpecimens, numberDigitizedSpecimens, numberObjects,
	numberDigitizedObjects;

	public short earliestYear, latestYear;

	@Column(columnDefinition = "text")
	public String expeditions;
	
	@Column(columnDefinition = "text")
	public String remarkablePersons;
	
	public String administrativeContact;

	@Email
	@Required
	@Column(nullable = false)	
	public String administrativeEmail;

//	Note: I'd love to add @Column(nullable = false) or @Basic(optional = false)
//	but it doesn't allow update (validation failed for models.Dataset) 
	public Date creationDate;
	
//	Note: I'd love to add @Column(nullable = false) or @Basic(optional = false)
//	but it doesn't allow update (validation failed for models.Dataset) 
	public Date updateDate;	
	
	@ManyToOne
	public Organization organization;	
	
	@OneToMany(mappedBy="collection", cascade=CascadeType.ALL)
	public List<Dataset> dataset;	
	
	public static Finder<Long, Collectio> find = new Finder(Long.class,
			Collectio.class);
	
	public static List<Collectio> all() {
		return find.all();
	}
	
	public static Map<String, String> names() {
		LinkedHashMap<String, String> names = new LinkedHashMap<String, String>();

		for(Collectio c : Collectio.find.orderBy("name").findList()) {
			names.put(c.id.toString(), c.name);
		}
		
		return names;
	}
	/**
	 * Delete a Collectio
	 * @param id
	 */
	public static void delete(Long id) {
		find.ref(id).delete();
	}	

	/**
     * Return a page of Collectiones
     *
     * @param page Page to display
     * @param pageSize Number of datasets per page
     * @param sortBy Dataset property used for sorting
     * @param order Sort order (either or asc or desc)
     * @param filter Filter applied on the name column
     */	
	public static Page<Collectio> page(int page, int pageSize, String sortBy, String order,
			String filter) {
		return
	        find.where()
	        .ilike("name", "%" + filter + "%")
	        .orderBy(sortBy + " " + order)
	        .findPagingList(pageSize)
	        .getPage(page);		
	}	
	
	@Override
	public void save() {
		creationDate();
		super.save();
	}
	
	@Override
	public void update(Object arg0) {
		updateDate();
		super.update(arg0);
	}
	
	@PrePersist
	void creationDate() {
		this.creationDate = this.updateDate = new Date();
	}
	
	@PreUpdate
	void updateDate() {
		this.updateDate = new Date();
	}	
}
