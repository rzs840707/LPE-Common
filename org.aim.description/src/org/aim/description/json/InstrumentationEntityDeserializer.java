package org.aim.description.json;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.aim.description.InstrumentationEntity;
import org.aim.description.probes.MeasurementProbe;
import org.aim.description.restrictions.Restriction;
import org.aim.description.scopes.Scope;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;

public class InstrumentationEntityDeserializer extends JsonDeserializer<InstrumentationEntity<?>> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public InstrumentationEntity<?> deserialize(JsonParser parser, DeserializationContext context) throws IOException,
			JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		ObjectCodec oc = parser.getCodec();
		JsonNode node = oc.readTree(parser);
		
		Class<? extends Scope> scopeClass = mapper.treeToValue(node.get("scopeClass"), Class.class);
		Scope scope = mapper.treeToValue(node.get("scope"), scopeClass);
		InstrumentationEntity entity = new InstrumentationEntity(scope);
		
		Restriction restriction = mapper.treeToValue(node.get("localRestriction"), Restriction.class);
		entity.setLocalRestriction(restriction);
		
		MeasurementProbe[] probesAsArray = mapper.treeToValue(node.get("probesAsArray"), MeasurementProbe[].class);
		Set<MeasurementProbe> probes = new HashSet<>();
		for (MeasurementProbe mp : probesAsArray) {
			probes.add(mp);
		}
		entity.getProbes().addAll(probes);
		
		return entity;
	}

}
