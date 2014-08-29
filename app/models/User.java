package models;

import java.util.*;
import javax.persistence.*;

import org.mindrot.jbcrypt.BCrypt;

import play.Logger;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;
import play.data.validation.Constraints.*;

import com.avaje.ebean.*;

/**
 * User Entity managed by Ebean
 * @author korbinus
 *
 */
@Entity
//public class User extends Model {
public class User extends Model {
	
	@Id
	public Long id;
	
	/**
	 * Username: must be unique and not null
	 */
	@Required
	@Column(unique = true, nullable = false)
	public String userName;

	/**
	 * Name
	 */
	public String name;
	
	/**
	 * email: must be unique and not null
	 */
	@Email
	@Required
	@Column(unique = true, nullable = false)
	public String email;

	/**
	 * password: the concatenation of the password with the salt will be 
	 * saved encrypted
	 */
	@MinLength(6)
	@MaxLength(32)
	@Column(nullable = false)
	public String password;

	
	/**
	 * Rights
	 */
	@Column(nullable = false)
	public byte rights;
	
	public static Finder<Long, User> find = new Finder(Long.class,
			User.class);
	
	public static List<User> all() {
		return find.all();
	}
	
	/**
	 * Return a page of users
	 * 
     * @param page Page to display
     * @param pageSize Number of datasets per page
     * @param sortBy Dataset property used for sorting
     * @param order Sort order (either or asc or desc)
     * @param filter Filter applied on the name column
	 */
	public static Page<User> page (int page, int pageSize, String sortBy, 
									String order, String filter) {
		return 
			find.where()
				.ilike("name", "%" + filter + "%")
				.orderBy(sortBy + " " + order)
				.findPagingList(pageSize)
				.getPage(page);
	}
	
	/**
	 * Delete an user
	 * @param id Id of the user to be removed from the database
	 */
	public static void delete(Long id) {
		find.ref(id).delete();
	}
	
	/**
	 * Retrieve a User from email.
	 * @param email
	 * @return User record
	 */
    public static User findByEmail(String email) {
    	Logger.info("Looking for user " + email);
        return find.where().eq("email", email).findUnique();
    }

    /**
	 * Encrypt the received password using the BCrypt encryption.
	 * Note that you can easily increase the complexity by changing its value
	 * @param password
	 * @return String
	 */
	public static String encrypt(String password)
	{
		int complexity = 12;
		
		return BCrypt.hashpw(password, 
				BCrypt.gensalt(complexity));
	}

	/**
	 * Authenticate a User.
	 * @param email
	 * @param password
	 * @return User information if authenticated, otherwise null
	 */
	public static User authenticate(String email, String password) {
		//find user by email
		User user = findByEmail(email);
		if(user != null) {
			Logger.info("User " + email + " found");
			// then check the password
			if(BCrypt.checkpw(password, user.password)) {
				Logger.info("Authentication success");
				return user;
			} else { 
				Logger.info("Authentication error: wrong password");
				return null;
			}
		} else {
			Logger.info("Authentication error: user " + email + " not found");
			// else if we didn't find the user return null
			return null;
		}
    }
	
	/**
	 * Has the user admin rights?
	 * @param String email
	 * @return boolean
	 */
	public static boolean isAdmin(String email) {
		return (find.where()
				.eq("email",email)
				.eq("rights",4)
				.findRowCount()) > 0
				;
	}
}
