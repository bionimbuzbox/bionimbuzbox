package org.bionimbuzbox.dao.elasticsearch;

import java.util.logging.Logger;
import org.bionimbuzbox.dao.DAODriver;
import org.bionimbuzbox.dao.EventHandlerDAO;
import org.bionimbuzbox.model.EventHandler;

public class EventHandlerDAOImpl extends ElasticSearchDAOImpl<EventHandler> implements EventHandlerDAO  {
	private static final Logger LOGGER = Logger.getLogger(EventHandlerDAOImpl.class.getName());
	private static final String ES_INDEX = "event";
	private static final String ES_TYPE = "handler";
		
	public EventHandlerDAOImpl(DAODriver driver) {
		super(EventHandler.class, driver, ES_INDEX, ES_TYPE);
	}
	 
}
