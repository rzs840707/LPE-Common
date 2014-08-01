package org.aim.description.json;

import java.io.IOException;

import org.aim.description.InstrumentationEntity;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class InstrumentationEntitySerializer extends JsonSerializer<InstrumentationEntity<?>> {

	@Override
	public void serialize(InstrumentationEntity<?> entity, JsonGenerator generator, SerializerProvider provider)
			throws IOException, JsonProcessingException {
		generator.writeStartObject();
		generator.writeObjectField("localRestriction", entity.getLocalRestriction());
		generator.writeObjectField("scope", entity.getScope());
		generator.writeObjectField("scopeClass", entity.getScope().getClass());
		generator.writeObjectField("probesAsArray", entity.getProbes().toArray());
		generator.writeEndObject();
	}

}
