package org.aim.description.servlet;

import org.aim.description.InstrumentationDescription;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

public class TestIDMServlet {

	public void doService(Request req, Response resp) throws Exception {
		resp.setContentType("application/json");

		ObjectMapper mapper = new ObjectMapper();
		try {
			InstrumentationDescription description = mapper.readValue(req.getInputStream(),
					InstrumentationDescription.class);
			mapper.writeValue(resp.getOutputStream(), description);
		} catch (JsonParseException jpe) {
			jpe.printStackTrace();
			throw new IllegalArgumentException("Incoming description is invalid!");
		} catch (JsonMappingException jme) {
			jme.printStackTrace();
			throw new IllegalArgumentException("Incoming description is invalid!");
		}
	}
}
