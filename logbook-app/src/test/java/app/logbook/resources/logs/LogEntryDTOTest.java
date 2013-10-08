package app.logbook.resources.logs;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mosaic.lang.time.DTM;
import framework.river.gson.DTMGsonCodec;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class LogEntryDTOTest {
    private Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter( DTM.class, new DTMGsonCodec() )
            .create();

    @Test
    public void toJson() {
        LogEntryDTO dto = new LogEntryDTO();

        dto.isDroppableMessage = false;
        dto.message            = "hello";
        dto.messageType        = "type";
        dto.requestId          = "requestId";
        dto.userId             = "userId1";
        dto.whenDtm            = new DTM(1000);

        String json = gson.toJson(dto);

        assertEquals( "{\"message_type\":\"type\",\"when_dtm\":1000,\"message\":\"hello\",\"user_id\":\"userId1\",\"request_id\":\"requestId\",\"is_droppable_message\":false}", json );
    }

    @Test
    public void parseJson() {
        String json = "{\"message_type\":\"type\",\"when_dtm\":1000,\"message\":\"hello\",\"user_id\":\"userId1\",\"request_id\":\"requestId\",\"is_droppable_message\":false}";

        LogEntryDTO dto = gson.fromJson(json, LogEntryDTO.class);

        assertEquals( false, dto.isDroppableMessage );
        assertEquals( "hello", dto.message );
        assertEquals( "type", dto.messageType );
        assertEquals( "requestId", dto.requestId );
        assertEquals( "userId1", dto.userId );
        assertEquals( new DTM(1000), dto.whenDtm );
    }

    @Test
    public void parseJsonWithMissingMandatoryBooleanFlag_expectBooleanToDefaultToFalse() {  // todo we want it to default to true
        String json = "{\"message_type\":\"type\",\"when_dtm\":1000,\"message\":\"hello\",\"user_id\":\"userId1\",\"request_id\":\"requestId\"}";

        LogEntryDTO dto = gson.fromJson(json, LogEntryDTO.class);

        assertEquals( false, dto.isDroppableMessage );
        assertEquals( "hello", dto.message );
        assertEquals( "type", dto.messageType );
        assertEquals( "requestId", dto.requestId );
        assertEquals( "userId1", dto.userId );
        assertEquals( new DTM(1000), dto.whenDtm );
    }

}
