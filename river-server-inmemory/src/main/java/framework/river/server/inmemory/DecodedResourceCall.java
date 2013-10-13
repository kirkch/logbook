package framework.river.server.inmemory;


import com.mosaic.lang.Lockable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 *
 */
@SuppressWarnings("unchecked")
public class DecodedResourceCall extends Lockable<DecodedResourceCall> {

    private String             relativeURL;
    private Class              resourceHandler;
    private Map<String,String> parameters;


    public DecodedResourceCall( Class resourceHandler ) {
        this.resourceHandler = resourceHandler;
    }

    public DecodedResourceCall( String relativeURL, Class resourceHandler ) {
        this( relativeURL, resourceHandler, new HashMap() );
    }

    public DecodedResourceCall(String relativeURL, Class resourceHandler, Map<String,String> parameters) {
        this.relativeURL     = relativeURL;
        this.resourceHandler = resourceHandler;
        this.parameters      = parameters;
    }


    public String getRelativeURL() {
        return relativeURL;
    }

    public Class getResourceHandler() {
        return resourceHandler;
    }

    public Map<String,String> getParameters() {
        return parameters;
    }


    public void setRelativeURL(String relativeURL) {
        throwIfLocked();

        this.relativeURL = relativeURL;
    }

    public void setResourceHandler(Class resourceHandler) {
        throwIfLocked();

        this.resourceHandler = resourceHandler;
    }

    public void setParameters(Map<String, String> parameters) {
        throwIfLocked();

        this.parameters = parameters;
    }

    public void appendParameter( String key, String value ) {
        throwIfLocked();

        parameters.put( key, value );
    }

    public int hashCode() {
        return relativeURL.hashCode();
    }

    public boolean equals( Object o ) {
        if ( !(o instanceof  DecodedResourceCall) ) {
            return false;
        } else if ( o == this ) {
            return true;
        }

        DecodedResourceCall other = (DecodedResourceCall) o;
        return Objects.equals(this.relativeURL, other.relativeURL)
                && Objects.equals(this.resourceHandler, other.resourceHandler)
                && Objects.equals(this.parameters, other.parameters);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append( "DecodedResourceCall(" );
        buf.append( relativeURL );
        buf.append( ", " );
        buf.append( resourceHandler );
        buf.append( ", " );
        buf.append( parameters );
        buf.append( ")" );

        return buf.toString();
    }

}
