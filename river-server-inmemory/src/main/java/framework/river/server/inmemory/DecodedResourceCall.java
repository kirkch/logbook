package framework.river.server.inmemory;


import com.mosaic.lang.Lockable;

import java.util.*;


/**
 *
 */
@SuppressWarnings("unchecked")
public class DecodedResourceCall extends Lockable<DecodedResourceCall> {

    private String             relativeURL;
    private Class              resourceHandler;
    private Map<String,Object> parameters;

    private List<String>       diagnosticMessages = new ArrayList(3);


    public DecodedResourceCall( Class resourceHandler ) {
        this.resourceHandler = resourceHandler;
        this.parameters      = new HashMap();
    }

    public DecodedResourceCall( String relativeURL, Class resourceHandler ) {
        this( relativeURL, resourceHandler, new HashMap() );
    }

    public DecodedResourceCall(String relativeURL, Class resourceHandler, Map<String,Object> parameters) {
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

    public Map<String,Object> getParameters() {
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

    public void setParameters(Map<String, Object> parameters) {
        throwIfLocked();

        this.parameters = parameters;
    }

    public void appendParameter( String key, Object value ) {
        throwIfLocked();

        parameters.put( key, value );
    }

    public void appendErrorMessage( String msg ) {
        throwIfLocked();

        diagnosticMessages.add( msg );
    }

    public List<String> getDiagnosticMessages() {
        return Collections.unmodifiableList(diagnosticMessages);
    }

    public boolean hasErrored() {
        return !diagnosticMessages.isEmpty();
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
                && Objects.equals(this.parameters, other.parameters)
                && Objects.equals(this.diagnosticMessages, other.diagnosticMessages);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append( "DecodedResourceCall(relativeURL=" );
        buf.append( relativeURL );
        buf.append( ", resourceHandler=" );
        buf.append( resourceHandler );
        buf.append( ", parameters=" );
        buf.append( parameters );
        buf.append( ", diagnosticMessages=" );
        buf.append( diagnosticMessages );
        buf.append( ")" );

        return buf.toString();
    }

}
