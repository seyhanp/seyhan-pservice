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

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

import com.seyhanproject.pserver.Document;
import com.seyhanproject.pserver.Printing;

public class Consumer {

	private static final Logger log = Logger.getLogger(Consumer.class);

    public Consumer() {
    	log.info("Messages queue will be esablished over " + Property.getIP());

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(Property.getIP());
        Connection connection;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(session.createQueue(Property.getFilterTargetNames()));

            log.info("Messages queue has esablished successfuly!");
            while (true) {
		        Message message = consumer.receive(); 
		        if (message != null && message instanceof ObjectMessage) {
	            	ObjectMessage msg = (ObjectMessage) message;
	                Document doc = (Document) msg.getObject();
	                
	                /*
	                 * Override Path
	                 */
	                String newPath = null;
	                if (checkFilters(doc)) {
                    	switch (doc.targetType) {
            				case Document.FILE: {
            					newPath = Property.getOverrideFILE();
            					break;
            				}
            				case Document.DOT_MATRIX: {
            					newPath = Property.getOverrideDOT_MATRIX();
            					break;
            				}
            				case Document.LASER: {
            					newPath = Property.getOverrideLASER();
            					break;
            				}
            			}
                    	if (newPath != null) doc.path = newPath;
		                String path = Printing.print(doc);

		                log.info(String.format("Document properties { user: %s, ip: %s, workspace : %s, type : %s, id: %d, target: %s, path: %s }" , 
		                						doc.username, doc.userIp, doc.workspace, doc.right, doc.id, doc.targetName, path));
	                }
	            }
            }
        } catch (Exception e) {
			log.error("ERROR", e);
        }
    }
    
    private boolean checkFilters(Document doc) {
    	String check = null;

    	/*
    	 * Username
    	 */
    	check = Property.getFilterUsernames();
        if (check != null) {
        	if (check.indexOf(doc.username) < 0) return false;
        }

    	/*
    	 * User IP
    	 */
    	check = Property.getFilterUserips();
        if (check != null) {
        	if (check.indexOf(doc.userIp) < 0) return false;
        }

    	/*
    	 * User IP Regex
    	 */
    	check = Property.getFilterUseripRegex();
        if (check != null) {
        	if (! doc.userIp.matches(check)) return false;
        }

    	/*
    	 * Workspace
    	 */
    	check = Property.getFilterWorkspaces();
        if (check != null) {
        	if (check.indexOf(doc.workspace) < 0) return false;
        }

    	/*
    	 * Document Type
    	 */
    	check = Property.getFilterDocTypes();
        if (check != null) {
        	if (check.indexOf(doc.right) < 0) return false;
        }

    	/*
    	 * Target Type
    	 */
        check = Property.getFilterTargetTypes();
        if (check != null) {
        	switch (doc.targetType) {
				case Document.FILE: {
					if (check.indexOf("FILE") < 0) return false;
					break;
				}
				case Document.DOT_MATRIX: {
					if (check.indexOf("DOT_MATRIX") < 0) return false;
					break;
				}
				case Document.LASER: {
					if (check.indexOf("LASER") < 0) return false;
					break;
				}
			}
        }
        
        return true;
    }

}
