package com.googlecode.npackdweb;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

/**
 * Save <file> in a package version.
 */
public class PackageVersionFileSaveAction extends Action {
	/**
	 * -
	 */
	public PackageVersionFileSaveAction() {
		super("^/pv-file/save$", ActionSecurityType.ADMINISTRATOR);
	}

	@Override
	public Page perform(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String name = req.getParameter("name");
		String path = req.getParameter("path");
		String content = req.getParameter("content");

		NWUtils.LOG.warning(content + path);

		Objectify ofy = ObjectifyService.begin();
		PackageVersion r = ofy.get(new Key<PackageVersion>(
				PackageVersion.class, name));

		int index = r.filePaths.indexOf(path);
		if (index >= 0) {
			if (content.isEmpty()) {
				r.filePaths.remove(index);
				r.fileContents.remove(index);
			} else {
				r.fileContents.set(index, content);
			}
		} else {
			if (!content.isEmpty()) {
				r.filePaths.add(path);
				r.fileContents.add(content);
			}
		}
		ofy.put(r);

		int pos = name.indexOf("@");
		String package_ = name.substring(0, pos);
		String version = name.substring(pos + 1);
		resp.sendRedirect("/p/" + package_ + "/" + version);

		return null;
	}
}