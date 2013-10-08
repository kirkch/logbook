package framework.river.server.inmemory;

import framework.river.RiverServer;
import framework.river.lang.Nullable;

/**
 * Manages url mapping to resource handler classes.
 */
@SuppressWarnings("unchecked")
public class ResourceHandlerRegistry {

    public RiverServer addResource( String encodedURLRef, Class<?> resourceClass ) {
        return null;
    }

    public RiverServer addAlias( String sourceEncodedURLRef, String destinationEncodedUrlRef ) {
        return null;
    }

    public Nullable<Class> lookupResourceHandler( String relativeURL ) {
        return Nullable.NULL;
    }

}
