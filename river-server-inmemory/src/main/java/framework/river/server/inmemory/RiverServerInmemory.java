package framework.river.server.inmemory;

import framework.river.RiverRequest;
import framework.river.RiverResponse;
import framework.river.RiverServer;
import framework.river.RiverSystem;
import framework.river.http.HttpMethodEnum;
import framework.river.lang.Future;
import framework.river.lang.Nullable;


/**
 *
 */
@SuppressWarnings("unchecked")
public class RiverServerInmemory implements RiverServer {


    private RiverSystem system;

    public RiverServerInmemory( RiverSystem system ) {
        this.system = system;
    }


    public RiverServer addResource( String encodedURLRef, Class<?> resourceClass ) {
        return null;
    }

    public RiverServer addAlias( String sourceEncodedURLRef, String destinationEncodedUrlRef ) {
        return null;
    }

    public Future<Void> start() {
        return Future.successful(null);
    }

    public Future<Void> stop() {
        return Future.successful(null);
    }



    public RiverRequest GET( String resourceRef ) {
        return new RiverRequest( system.currentDTM(), system.generateUUID(), resourceRef, HttpMethodEnum.GET );
    }

    public RiverRequest PUT( String resourceRef, Nullable<?> body ) {
        return new RiverRequest( system.currentDTM(), system.generateUUID(), resourceRef, HttpMethodEnum.PUT )
                .withRequestBodyNbl( body );
    }

    public RiverRequest POST( String resourceRef, Nullable<?> body ) {
        return new RiverRequest( system.currentDTM(), system.generateUUID(), resourceRef, HttpMethodEnum.POST )
                .withRequestBodyNbl( body );
    }

    public RiverRequest PATCH( String resourceRef, Nullable<?> body ) {
        return new RiverRequest( system.currentDTM(), system.generateUUID(), resourceRef, HttpMethodEnum.PATCH )
                .withRequestBodyNbl( body );
    }

    public RiverRequest HEAD( String resourceRef ) {
        return new RiverRequest( system.currentDTM(), system.generateUUID(), resourceRef, HttpMethodEnum.HEAD );
    }

    public RiverRequest DELETE( String resourceRef ) {
        return new RiverRequest( system.currentDTM(), system.generateUUID(), resourceRef, HttpMethodEnum.DELETE );
    }

    public RiverRequest SUBSCRIBE( String resourceRef ) {
        return new RiverRequest( system.currentDTM(), system.generateUUID(), resourceRef, HttpMethodEnum.SUBSCRIBE );
    }

    public RiverRequest UNSUBSCRIBE( String resourceRef ) {
        return new RiverRequest( system.currentDTM(), system.generateUUID(), resourceRef, HttpMethodEnum.UNSUBSCRIBE );
    }




    public Future<RiverResponse> process( RiverRequest req ) {
        return Future.successful(
                RiverResponse.noResourceFound(req, Nullable.createNullable("no resource handler registered for '"+req.getResourceRef()+"'"))
        );
    }


}
