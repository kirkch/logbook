package framework.river;

import com.mosaic.lang.time.DTM;
import framework.river.http.HttpMethodEnum;
import com.mosaic.lang.Nullable;

import java.util.Collections;
import java.util.Map;


/**
 *
 */
public class RiverRequest {

    private DTM              startDTM;
    private String           requestId;
    private Nullable<String> userIdNbl;
    private Nullable<String> userNameNbl;

    private String           resourceRef;
    private long             resourceModificationSequence;

    private HttpMethodEnum   httpMethod;
    private Nullable<?>      requestBodyNbl;

    private Nullable<DTM>    ifModifiedSinceNbl;

    private Map<String,?>    queryParameters = Collections.emptyMap();

    public RiverRequest( DTM startDTM, String requestId, String resourceRef, HttpMethodEnum httpMethod ) {
        this.startDTM    = startDTM;
        this.requestId   = requestId;
        this.resourceRef = resourceRef;
        this.httpMethod  = httpMethod;
    }



    public DTM getStartDTM() {
        return startDTM;
    }

    public void setStartDTM(DTM startDTM) {
        this.startDTM = startDTM;
    }

    public RiverRequest withStartDTM(DTM startDTM) {
        this.startDTM = startDTM;

        return this;
    }



    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public RiverRequest withRequestId(String requestId) {
        this.requestId = requestId;

        return this;
    }



    public Nullable<String> getUserIdNbl() {
        return userIdNbl;
    }

    public void setUserIdNbl(Nullable<String> userIdNbl) {
        this.userIdNbl = userIdNbl;
    }

    public RiverRequest withUserIdNbl(Nullable<String> userIdNbl) {
        this.userIdNbl = userIdNbl;

        return this;
    }



    public Nullable<String> getUserNameNbl() {
        return userNameNbl;
    }

    public void setUserNameNbl(Nullable<String> userNameNbl) {
        this.userNameNbl = userNameNbl;
    }

    public RiverRequest withUserNameNbl(Nullable<String> userNameNbl) {
        this.userNameNbl = userNameNbl;

        return this;
    }



    public String getResourceRef() {
        return resourceRef;
    }

    public void setResourceRef(String resourceRef) {
        this.resourceRef = resourceRef;
    }

    public RiverRequest withResourceRef(String resourceRef) {
        this.resourceRef = resourceRef;

        return this;
    }



    public long getResourceModificationSequence() {
        return resourceModificationSequence;
    }

    public void setResourceModificationSequence(long resourceModificationSequence) {
        this.resourceModificationSequence = resourceModificationSequence;
    }

    public RiverRequest withResourceModificationSequence(long resourceModificationSequence) {
        this.resourceModificationSequence = resourceModificationSequence;

        return this;
    }



    public HttpMethodEnum getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethodEnum httpMethod) {
        this.httpMethod = httpMethod;
    }

    public RiverRequest withHttpMethod(HttpMethodEnum httpMethod) {
        this.httpMethod = httpMethod;

        return this;
    }



    public Nullable<?> getRequestBodyNbl() {
        return requestBodyNbl;
    }

    public void setRequestBodyNbl(Nullable<?> requestBodyNbl) {
        this.requestBodyNbl = requestBodyNbl;
    }

    public RiverRequest withRequestBodyNbl(Nullable<?> requestBodyNbl) {
        this.requestBodyNbl = requestBodyNbl;

        return this;
    }



    public Nullable<DTM> getIfModifiedSinceNbl() {
        return ifModifiedSinceNbl;
    }

    public void setIfModifiedSinceNbl(Nullable<DTM> ifModifiedSinceNbl) {
        this.ifModifiedSinceNbl = ifModifiedSinceNbl;
    }

    public RiverRequest withIfModifiedSinceNbl(Nullable<DTM> ifModifiedSinceNbl) {
        this.ifModifiedSinceNbl = ifModifiedSinceNbl;

        return this;
    }


    @SuppressWarnings("unchecked")
    public <T> Nullable<T> getQueryParameter( String key ) {
        return Nullable.createNullable( (T) queryParameters.get(key) );
    }

    public RiverRequest withQueryParameters( Map<String,?> params ) {
        this.queryParameters = params;

        return this;
    }

}
