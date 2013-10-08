package framework.river;

import com.mosaic.lang.time.DTM;
import framework.river.lang.Nullable;

/**
 *
 */
public interface RiverResponseCallback {

    public void noResourceFound( String requestId, Nullable<String> userIdNbl, Nullable<String> userNameNbl, Nullable<String> diagnosticMessageNbl );

    public void remoteHost( String requestId, Nullable<String> userIdNbl, Nullable<String> userNameNbl, Nullable<String> diagnosticMessageNbl, String remoteHost, int remotePort );

    public void processed( String requestId, Nullable<String> userIdNbl, Nullable<String> userNameNbl, Nullable<String> diagnosticMessageNbl, int httpStatus, Nullable<Object> bodyNbl, long cacheForSeconds, Nullable<DTM> lastModifiedDtmNbl );

}
