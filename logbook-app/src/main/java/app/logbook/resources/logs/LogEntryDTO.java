package app.logbook.resources.logs;


import com.mosaic.lang.time.DTM;

/**
 *
 */
public class LogEntryDTO {

    public String messageType;

    public DTM whenDtm;
    public String message;

    public String userId;
    public String requestId;

    public boolean isDroppableMessage;


    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append( "LogEntryDTO(" );
        buf.append( messageType );
        buf.append( "," );
        buf.append( message );
        buf.append( "," );
        buf.append( userId );
        buf.append( "," );
        buf.append( requestId );
        buf.append( ")" );

        return buf.toString();
    }
}
