package org.finos.symphony.toolkit.workflow.sources.symphony.handlers;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.response.Response;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.TagSupport;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.HashTag;
import org.finos.symphony.toolkit.workflow.sources.symphony.json.HeaderDetails;

public class HeaderTagResponseHandler implements ResponseHandler {

	/**
	 * This ensures that the JSON data being sent will contain a HeaderDetails
	 * object, which contains a list of {@link HashTag}s that need to be present in
	 * the message for indexing purposes.
	 */
	@Override
	public void accept(Response t) {

		if (t instanceof WorkResponse) {
			WorkResponse workResponse = (WorkResponse) t;

			HeaderDetails hd = (HeaderDetails) workResponse.getData().get(HeaderDetails.KEY);
			if (hd == null) {
				hd = new HeaderDetails();
				workResponse.getData().put(HeaderDetails.KEY, hd);
			}

			// make sure all tags are unique, maintain order from original.
			Set<HashTag> tags = new LinkedHashSet<>();
			tags.addAll(hd.getTags());
			
			// check through other stuff in the json response
			for (Object o2 : workResponse.getData().values()) {
				Work w = o2 != null ? o2.getClass().getAnnotation(Work.class) : null;
				if ((w != null) && (w.index())) {
					tags.addAll(TagSupport.classHashTags(o2));
				}
			}
			
			
			hd.setTags(new ArrayList<HashTag>(tags));

		}
	}

	@Override
	public int getOrder() {
		return MEDIUM_PRIORITY;
	}

}
