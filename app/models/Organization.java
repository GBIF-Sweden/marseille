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
 */
//TODO make Countries en entity with a dropdown list
@Entity
public class Organization extends Model{
	@Id
	public Long id;
	
	@Required
	@Column(unique = true, nullable = false)
	public String name;
	
	public String address;
	public String postBox;
	public String postCode;
	public String city;
	public String country;
	public String administrativeContact;
	public String webUrl;

	@Email
	@Required
	@Column(nullable = false)	
	public String administrativeEmail;
	
	public String technicalContact;
	
	@Email
	@Required
	@Column(nullable = false)	
	public String technicalEmail;

	@Column(unique = true, nullable = true)
	public String bciID;
	
	@Column(unique = true, nullable = true)
	public String gbrdsID;

//	Note: I'd love to add @Column(nullable = false) or @Basic(optional = false)
//	but it doesn't allow update (validation failed for models.Dataset) 
	public Date creationDate;
	
//	Note: I'd love to add @Column(nullable = false) or @Basic(optional = false)
//	but it doesn't allow update (validation failed for models.Dataset) 
	public Date updateDate;		
	
	@OneToMany(mappedBy="organization", cascade=CascadeType.ALL)
	public List<Dataset> dataset;

	@OneToMany(mappedBy="organization", cascade=CascadeType.ALL)
	public List<Collectio> collection;

	public static Finder<Long, Organization> find = new Finder(Long.class,
			Organization.class);	
	
	public static List<Organization> all() {
		return find.all();
	}
	
	public static Map<String, String> names() {
		LinkedHashMap<String, String> names = new LinkedHashMap<String, String>();

		for(Organization o : Organization.find.orderBy("name").findList()) {
			names.put(o.id.toString(), o.name);
		}
		
		return names;
	}
	
	/**
     * Return a page of organizations
     *
     * @param page Page to display
     * @param pageSize Number of datasets per page
     * @param sortBy Dataset property used for sorting
     * @param order Sort order (either or asc or desc)
     * @param filter Filter applied on the name column
     */	
	public static Page<Organization> page(int page, int pageSize, String sortBy, String order,
			String filter) {
		return
	        find.where()
	        .ilike("name", "%" + filter + "%")
	        .orderBy(sortBy + " " + order)
	        .findPagingList(pageSize)
	        .getPage(page);		
	}

	/**
	 * Delete an organization
	 * @param id
	 */
	public static void delete(Long id) {
		find.ref(id).delete();
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
