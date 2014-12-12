/**
* Copyright (c) 2015 Mustafa DUMLUPINAR, mdumlupinar@gmail.com
*
* This file is part of seyhan project.
*
* seyhan is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package com.seyhanproject.pservice;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Property {
	
	private static final Logger log = Logger.getLogger(Property.class);

	private static Properties prop;
	
	static {
		InputStream input = null;
		try {
			input = new FileInputStream("./conf/pservice.properties");
			prop = new Properties();
			prop.load(input);
		} catch (Exception e) {
			log.error("ERROR", e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					log.error("ERROR", e);
				}
			}
		}
	}

	public static String getIP() {
		return prop.getProperty("app.queue.uri");
	}

	public static String getAppPort() {
		return prop.getProperty("app.port");
	}

	public static String getFilterUsernames() {
		String val = prop.getProperty("filter.usernames");
		if (val != null && val.trim().isEmpty()) return null;
		return val;
	}

	public static String getFilterUserips() {
		String val = prop.getProperty("filter.userips");
		if (val != null && val.trim().isEmpty()) return null;
		return val;
	}
	
	public static String getFilterUseripRegex() {
		String val = prop.getProperty("filter.userip.regex");
		if (val != null && val.trim().isEmpty()) return null;
		return val;
	}

	public static String getFilterWorkspaces() {
		String val = prop.getProperty("filter.workspaces");
		if (val != null && val.trim().isEmpty()) return null;
		return val;
	}

	public static String getFilterTargetNames() {
		return prop.getProperty("filter.target.names");
	}

	public static String getFilterTargetTypes() {
		String val = prop.getProperty("filter.target.types");
		if (val != null && val.trim().isEmpty()) return null;
		return val;
	}

	public static String getFilterDocTypes() {
		String val = prop.getProperty("filter.doc.types");
		if (val != null && val.trim().isEmpty()) return null;
		return val;
	}

	public static String getOverrideFILE() {
		String val = prop.getProperty("override.path.FILE");
		if (val != null && val.trim().isEmpty()) return null;
		return val;
	}

	public static String getOverrideDOT_MATRIX() {
		String val = prop.getProperty("override.path.DOT_MATRIX");
		if (val != null && val.trim().isEmpty()) return null;
		return val;
	}

	public static String getOverrideLASER() {
		String val = prop.getProperty("override.name.LASER");
		if (val != null && val.trim().isEmpty()) return null;
		return val;
	}

}
