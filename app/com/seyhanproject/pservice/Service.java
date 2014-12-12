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

import java.io.BufferedReader;
import java.io.Console;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Service {

	private static final Logger log = Logger.getLogger(Service.class);

	public static void main(String[] args) {
		String username = null;
		String password = null;
		boolean isConsoleNull = false;

		try {
			PropertyConfigurator.configure("./conf/log4j.properties");

			Console console = System.console();
			if (console != null) {
				username = console.readLine("userame: ");
				char[] pwd = console.readPassword("password for %s: ", username);
				password = new String(pwd);
				Arrays.fill(pwd, ' ');
			} else {
				isConsoleNull = true;
			}
		} catch (Exception e) {
			log.error("ERROR", e);
		}

		if (isConsoleNull) {
			log.warn("Console is not available in this context, please try in command line!");
		} else if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
			log.warn("username and password cannot be null!");
		} else {
			if (checkUserRight(username, password)) new Consumer();
		}
	}
	
	private static boolean checkUserRight(String username, String password) {
		String authURL = buildAppAuthenticationURL(username, password);
		if (authURL.length() < 1) {
			log.warn("Authentication URL is null!!!");
			return false;
		}
		String urlWithoutPassword = authURL.substring(0, authURL.indexOf("&"));
		log.info("Authentication URL : " + urlWithoutPassword);
		log.info("Connecting to the app. server, please wait...");

		HttpURLConnection connection = null;
	    try {
	        URL url = new URL(authURL);
	        connection = (HttpURLConnection) url.openConnection();
	        connection.connect();
	        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        String result = in.readLine();
	        in.close();
	        log.info("For " + username + " user, authentication result is { " + result + " } ");
	        return "ok".equals(result);
	    } catch (FileNotFoundException e) {
	    	log.error("Wrong URL : " + urlWithoutPassword);
	    } catch (Exception e) {
	    	if ("Connection refused".equals(e.getMessage())) {
	    		log.error("There is no connection or application isn't started!");
	    	} else {
	    		log.error(e.getMessage().replaceAll("\\&password.*", ""), e);
	    	}
	    } finally {
	        if (null != connection) {
	        	connection.disconnect();
	        }
	    }

	    return false;
	}

	private static String buildAppAuthenticationURL(String username, String password) {
		StringBuilder result = new StringBuilder();
		if (Property.getIP() != null) {
			String[] rawParts = Property.getIP().split("//");
			if (rawParts != null && rawParts.length == 2) {
				String[] orgParts = rawParts[1].split("\\:");
				if (orgParts != null) {
					result.append("http://");
					result.append(orgParts[0]);
					if (Property.getAppPort() != null) {
						result.append(":");
						result.append(Property.getAppPort());
					}
					result.append("/as/pservice/check_user?username=");
					result.append(username);
					result.append("&password=");
					result.append(password);
				}
			}
		}

		return result.toString();
	}

}
