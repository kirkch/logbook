package framework.river.gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.mosaic.lang.time.DTM;

/**
 *
 */
public class GsonFactory {

    public static GsonBuilder createBuilder() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(DTM.class, new DTMGsonCodec());
    }

}
