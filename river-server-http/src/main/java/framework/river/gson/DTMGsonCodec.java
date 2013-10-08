package framework.river.gson;

import com.google.gson.*;
import com.mosaic.lang.time.DTM;

import java.lang.reflect.Type;

/**
 *
 */
public class DTMGsonCodec implements JsonSerializer<DTM>, JsonDeserializer<DTM> {

    // todo 2012-04-23T18:25:43.511Z

    public JsonElement serialize( DTM dtm, Type type, JsonSerializationContext jsonSerializationContext ) {
        return new JsonPrimitive( dtm.getMillisSinceEpoch() );
    }

    public DTM deserialize( JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext ) throws JsonParseException {
        return new DTM( jsonElement.getAsJsonPrimitive().getAsLong() );
    }

}
