package models;

//import java.sql.Timestamp;
import java.util.*;
import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;
import play.data.validation.Constraints.*;

import com.avaje.ebean.*;

/**
 * Dataset Entity managed by Ebean
 * @author korbinus
 *
 */
@Entity
public class Dataset extends Model {

	@Id
	public Long id;

	@Required
	@Column(unique = true, nullable = false)
	public String code;
	@Required
	@Column(unique = true, nullable = false)
	public String name;
	public String webUrl, wsUrl;
	@Column(columnDefinition = "text")
	public String description;

	@Column(unique = true, nullable = true)
	public String gbrdsID;
		
	// Data calculated from the portal database, not to be filled by the end user
	public Long lastOffset;
	public Date lastOffsetDt;
	public Long nbRecords, nbCoordinated, nbSpecimens, nbObservations,
			nbLiving, nbIndividuals, nbLatEqLong, nbOutWorld, nbWithoutCountry,
			nbWithoutState;
	public short earliestYear, latestYear;
	public Date lastStatDt;
	// End of calculated data
	
	public String encoding;
//	Deprecated
//	public Long metaNbSpecimens, metaNbDigitizedSpecimens, metaNbObjects,
//			metaNbDigitizedObjects;

	public Long nbOriginalRecords;
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
	
	@ManyToOne
	public Collectio collection;
	
	public static Finder<Long, Dataset> find = new Finder(Long.class,
			Dataset.class);

	public static List<Dataset> all() {
		return find.all();
	}

    /**
     * Return a page of datasets
     *
     * @param page Page to display
     * @param pageSize Number of datasets per page
     * @param sortBy Dataset property used for sorting
     * @param order Sort order (either or asc or desc)
     * @param filter Filter applied on the name column
     */
    public static Page<Dataset> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where()
                .ilike("name", "%" + filter + "%")
                .orderBy(sortBy + " " + order)
                .fetch("organization")
                .findPagingList(pageSize)
                .getPage(page);
    }	
	
    /**
     * Delete a dataset
     * 
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