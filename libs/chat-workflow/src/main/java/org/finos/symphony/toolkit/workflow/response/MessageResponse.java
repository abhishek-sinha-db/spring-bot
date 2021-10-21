package org.finos.symphony.toolkit.workflow.response;

import java.util.Map;

import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Content;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.Word;

/**
 * A Response that includes a message {@link Content} to send back to the user.
 * 
 * @author rob@kite9.com
 *
 */
public class MessageResponse extends DataResponse {
	
	private final Content m;
	
	public MessageResponse(Addressable stream, Map<String, Object> data, Content m, String templateName) {
		super(stream, data, templateName);
		this.m = m;
	}
	
	public MessageResponse(Addressable stream, String textContent) {
		this(stream, Message.of(Word.build(textContent)));
	}
	

	public MessageResponse(Addressable stream, Content m) {
		super(stream, null, null);
		this.m = m;
	}
	

	public MessageResponse(Addressable stream, Content m, String template) {
		super(stream, null, template);
		this.m = m;
	}
	
	
	public Content getMessage() {
		return m;
	}

	@Override
	public String toString() {
		return "MessageResponse [m=" + m + ", getData()=" + getData() + ", getTemplate()=" + getTemplateName()
				+ ", getAddress()=" + getAddress() + "]";
	}

	
}