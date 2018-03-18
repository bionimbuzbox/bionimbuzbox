package org.bionimbuzbox.dao.elasticsearch;

import java.util.ArrayList;
import java.util.List;
import org.bionimbuzbox.model.Model;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticSearchDAOHelper {
	
  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchDAOHelper.class);

	public static void save(Model model, TransportClient client, String index, String type) {
		String id = model.getId();
		
		LOGGER.debug( String.format("Before Saving %s", new String(model.toJSONBytes())) );
		
		// remove id from _source
		model.setId(null);
		
		byte[] json = model.toJSONBytes();
		
		IndexResponse response = client.prepareIndex(index, type, id)
				.setSource(json)
				.get();
		
		LOGGER.debug( String.format("Saving %s", new String(json)) );
		
		model.setId(response.getId());
	}
	
	
	
	public static <T extends Model> T get(String id, Class<T> clazz, TransportClient client, String index, String type) {
		GetResponse response = client.prepareGet(index, type, id)
				.setOperationThreaded(false)
				.get();
		
		T newInstance = Model.buildFromJSONBytes(response.getSourceAsBytes(), clazz);
		if (newInstance == null) {
			return null;
		}
		newInstance.setId(response.getId());
		return newInstance;			
	}

	public static <T extends Model> List<T> getAll(Class<T> clazz, TransportClient client, String index, String type) {
		List<T> list = new ArrayList<>();
		SearchResponse response = client.prepareSearch(index)
				.setTypes(type)
				.setScroll(new TimeValue(60000))
				.setSize(100)
				.get();
		
		do {
		    for (SearchHit hit : response.getHits().getHits()) {
		        T newInstance = Model.buildFromJSONString(hit.getSourceAsString(), clazz);
		        newInstance.setId(hit.getId());
		        list.add(newInstance);
		    }

		    response = client.prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
		} while(response.getHits().getHits().length != 0);
		return list;
	}

	public static void remove(String id, TransportClient client, String index, String type) {
		DeleteResponse response = client.prepareDelete(index, type, id).get();
	}

}
