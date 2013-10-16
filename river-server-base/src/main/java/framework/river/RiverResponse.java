package framework.river;

import com.mosaic.lang.Validate;
import com.mosaic.lang.functional.Nullable;
import com.mosaic.lang.time.DTM;

/**
 *
 */
public class RiverResponse {

    public static RiverResponse noResourceFound( RiverRequest req, Nullable<String> diagnosticMessageNbl ) {
        return noResourceFound( req.getRequestId(), req.getUserIdNbl(), req.getUserNameNbl(), diagnosticMessageNbl );
    }

    public static RiverResponse noResourceFound( String requestId, Nullable<String> userIdNbl, Nullable<String> userNameNbl, Nullable<String> diagnosticMessageNbl ) {
        return new RiverResponse( RiverResponseEnum.NOT_FOUND, 404, -1, requestId, userIdNbl, userNameNbl, diagnosticMessageNbl );
    }


    public static RiverResponse remoteHost( RiverRequest req, Nullable<String> diagnosticMessageNbl, String remoteHost, int remotePort ) {
        return remoteHost( req.getRequestId(), req.getUserIdNbl(), req.getUserNameNbl(), diagnosticMessageNbl, remoteHost, remotePort );
    }

    public static RiverResponse remoteHost( String requestId, Nullable<String> userIdNbl, Nullable<String> userNameNbl, Nullable<String> diagnosticMessageNbl, String remoteHost, int remotePort ) {
        RiverResponse resp = new RiverResponse( RiverResponseEnum.REMOTE, 403, -1, requestId, userIdNbl, userNameNbl, diagnosticMessageNbl );

        resp.remoteHost      = remoteHost;
        resp.remotePort      = remotePort;

        return resp;
    }


    public static RiverResponse processedNoBody( RiverRequest req, Nullable<String> diagnosticMessageNbl, int httpStatus ) {
        return processedNoBody( req.getRequestId(), req.getUserIdNbl(), req.getUserNameNbl(), diagnosticMessageNbl, httpStatus );
    }

    public static RiverResponse processedNoBody( String requestId, Nullable<String> userIdNbl, Nullable<String> userNameNbl, Nullable<String> diagnosticMessageNbl, int httpStatus ) {
        long cacheForSeconds = -1;

        return new RiverResponse( RiverResponseEnum.REMOTE, httpStatus, cacheForSeconds, requestId, userIdNbl, userNameNbl, diagnosticMessageNbl );
    }


    public static RiverResponse processedWithBody( RiverRequest req, Nullable<String> diagnosticMessageNbl, int httpStatus, Nullable<Object> bodyNbl, long cacheForSeconds, Nullable<DTM> lastModifiedDtmNbl ) {
        return processedWithBody( req.getRequestId(), req.getUserIdNbl(), req.getUserNameNbl(), diagnosticMessageNbl, httpStatus, bodyNbl, cacheForSeconds, lastModifiedDtmNbl );
    }

    public static RiverResponse processedWithBody( String requestId, Nullable<String> userIdNbl, Nullable<String> userNameNbl, Nullable<String> diagnosticMessageNbl, int httpStatus, Nullable<Object> bodyNbl, long cacheForSeconds, Nullable<DTM> lastModifiedDtmNbl ) {
        RiverResponse resp = new RiverResponse( RiverResponseEnum.REMOTE, httpStatus, cacheForSeconds, requestId, userIdNbl, userNameNbl, diagnosticMessageNbl );

        resp.bodyNbl            = bodyNbl;
        resp.lastModifiedDtmNbl = lastModifiedDtmNbl;

        return resp;
    }



    // all
    private RiverResponseEnum           type;
    private String                      requestId;
    private Nullable<String>            userIdNbl;
    private Nullable<String>            userNameNbl;


    // remote
    private String remoteHost;
    private int    remotePort;


    // processed
    private int              httpStatus;
    private Nullable<Object> bodyNbl;
    private Nullable<String> diagnosticMessageNbl;
    private long             cacheForSeconds;
    private Nullable<DTM>    lastModifiedDtmNbl;


    private RiverResponse( RiverResponseEnum type, int httpStatus, long cacheForSeconds, String requestId, Nullable<String> userIdNbl, Nullable<String> userNameNbl, Nullable<String> diagnosticMessageNbl ) {
        Validate.notNull( requestId, "requestId" );

        this.type                 = type;
        this.httpStatus           = httpStatus;
        this.cacheForSeconds      = cacheForSeconds;
        this.requestId            = requestId;
        this.userIdNbl            = userIdNbl;
        this.userNameNbl          = userNameNbl;
        this.diagnosticMessageNbl = diagnosticMessageNbl;
    }


    public RiverResponseEnum getType() {
        return type;
    }

    public String getRequestId() {
        return requestId;
    }

    public Nullable<String> getUserIdNbl() {
        return userIdNbl;
    }

    public Nullable<String> getUserNameNbl() {
        return userNameNbl;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public Nullable<Object> getBodyNbl() {
        return bodyNbl;
    }

    public Nullable<String> getDiagnosticMessageNbl() {
        return diagnosticMessageNbl;
    }

    public long getCacheForSeconds() {
        return cacheForSeconds;
    }

    public Nullable<DTM> getLastModifiedDtmNbl() {
        return lastModifiedDtmNbl;
    }


    public void invoke( RiverResponseCallback callback ) {
        switch (type) {
            case NOT_FOUND:
                callback.noResourceFound(requestId, userIdNbl, userNameNbl, diagnosticMessageNbl);
                return;
            case REMOTE:
                callback.remoteHost(requestId, userIdNbl, userNameNbl, diagnosticMessageNbl, remoteHost, remotePort);
                return;
            case PROCESSED:
                callback.processed(requestId, userIdNbl, userNameNbl, diagnosticMessageNbl, httpStatus, bodyNbl, cacheForSeconds, lastModifiedDtmNbl);
                return;
            default:
                throw new UnsupportedOperationException("Unknown type: " + type);
        }
    }

}
