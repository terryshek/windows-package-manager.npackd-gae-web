package com.googlecode.npackdweb;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.appengine.api.search.Field;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;

/**
 * A package.
 */
@Entity
@Cached
public class Package {
	@Id
	/* internal name of the package like com.example.Test */
	public String name = "";

	public String title = "";
	public String url = "";
	public String description = "";
	public String icon = "";
	public String license = "";
	public String comment = "";

	/** last modification date */
	public Date lastModifiedAt = new Date();

	/** creation time */
	public Date createdAt = new Date();

	/**
	 * URL of the HTML/plain text page where the current version number is
	 * present
	 */
	public String discoveryPage = "";

	/** regular expression to discover the version number */
	public String discoveryRE = "";

	/**
	 * Pattern for the package binary download URL Available variables:
	 * ${{version}} ${{version2Parts}} ${{version3Parts}}
	 * ${{version2PartsWithoutDots}} ${{actualVersion}}
	 * ${{actualVersionWithoutDots}} ${{actualVersionWithUnderscores}}
	 * ${{match}}
	 */
	public String discoveryURLPattern = "";

	/** this package was created by this user */
	public User createdBy;

	/**
	 * For Objectify.
	 */
	public Package() {
	}

	/**
	 * @param name
	 *            full internal name of the package
	 */
	public Package(String name) {
		createdBy = UserServiceFactory.getUserService().getCurrentUser();
		if (createdBy == null)
			createdBy = new User("tim.lebedkov@gmail.com", "gmail.com");
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}

	public String getDescription() {
		return description;
	}

	public String getIcon() {
		return icon;
	}

	public String getLicense() {
		return license;
	}

	public String getComment() {
		return comment;
	}

	@PostLoad
	public void postLoad() {
		if (this.comment == null)
			this.comment = "";
		if (this.lastModifiedAt == null)
			this.lastModifiedAt = new Date();
		if (this.createdAt == null)
			this.createdAt = new Date(1355048474); // December 9, 2012, 11:21:14
		if (this.discoveryPage == null)
			this.discoveryPage = "";
		if (this.discoveryRE == null)
			this.discoveryRE = "";
		if (this.discoveryURLPattern == null)
			this.discoveryURLPattern = "";
		if (this.createdBy == null)
			this.createdBy = new User("tim.lebedkov@gmail.com", "gmail.com");
	}

	@PrePersist
	void onPersist() {
		DefaultServlet.dataVersion.incrementAndGet();
		this.lastModifiedAt = new Date();
	}

	/**
	 * <package>
	 * 
	 * @param d
	 *            XML document
	 * @return <package>
	 */
	public Element toXML(Document d) {
		Package p = this;
		Element package_ = d.createElement("package");
		package_.setAttribute("name", p.name);
		if (!p.title.isEmpty())
			NWUtils.e(package_, "title", p.title);
		if (!p.url.isEmpty())
			NWUtils.e(package_, "url", p.url);
		if (!p.description.isEmpty())
			NWUtils.e(package_, "description", p.description);
		if (!p.icon.isEmpty())
			NWUtils.e(package_, "icon", p.icon);
		if (!p.license.isEmpty())
			NWUtils.e(package_, "license", p.license);

		return package_;
	}

	/**
	 * @return created Key for this object
	 */
	public Key<Package> createKey() {
		return new Key<Package>(Package.class, this.name);
	}

	/**
	 * @return document for the search index
	 */
	public com.google.appengine.api.search.Document createDocument() {
		com.google.appengine.api.search.Document d = com.google.appengine.api.search.Document
		        .newBuilder()
		        .setId(this.name)
		        .addField(
		                Field.newBuilder().setName("title").setText(this.title))
		        .addField(
		                Field.newBuilder().setName("description").setText(
		                        this.description)).addField(
		                Field.newBuilder().setName("createdAt").setDate(
		                        this.createdAt)).build();
		return d;
	}
}
