package framework.river;

import framework.river.lang.Future;
import framework.river.lang.Nullable;

/**
 * The CyGrid Server.  Different implementations will offer different non-functional
 * characteristics and capabilities but they will implement this interface.
 */
public interface RiverServer {

    public RiverRequest GET( String resourceRef );
    public RiverRequest PUT( String resourceRef, Nullable<?> body );
    public RiverRequest POST( String resourceRef, Nullable<?> body );
    public RiverRequest PATCH( String resourceRef, Nullable<?> body );
    public RiverRequest HEAD( String resourceRef );
    public RiverRequest DELETE( String resourceRef );
    public RiverRequest SUBSCRIBE( String resourceRef );
    public RiverRequest UNSUBSCRIBE( String resourceRef );


    public Future<RiverResponse> process( RiverRequest req );



    public RiverServer addResource( String encodedURLRef, Class<?> resourceClass );

    public RiverServer addAlias( String sourceEncodedURLRef, String destinationEncodedUrlRef );

    public Future<Void> start();
    public Future<Void> stop();

}
