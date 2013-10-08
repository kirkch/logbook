package framework.river.server.inmemory;

import framework.river.RiverRequest;
import framework.river.RiverResponse;
import framework.river.RiverServer;
import framework.river.RiverSystem;
import framework.river.http.HttpMethodEnum;
import framework.river.lang.Future;

import static framework.river.lang.Nullable.NULL;

/**
 *
 */
@SuppressWarnings("unchecked")
public class RiverServerInmemory implements RiverServer {


    private RiverSystem system;

    public RiverServerInmemory( RiverSystem system ) {
        this.system = system;
    }

    public RiverRequest GET( String resourceRef ) {
        return new RiverRequest( system.currentDTM(), system.generateUUID(), resourceRef, HttpMethodEnum.GET );
    }



    public Future<RiverResponse> process( RiverRequest req ) {
        return Future.successful(RiverResponse.noResourceFound(req, NULL));
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

}
