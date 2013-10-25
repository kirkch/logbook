package framework.river.server.inmemory.resources;

import com.mosaic.lang.functional.Try;
import com.mosaic.lang.functional.TryNbl;


/**
 *
 */
public interface ResourceRepository {

    public TryNbl<Resource> fetchResourceFor( String canonicalResourcePath );

    public Try<Resource> fetchOrCreateResource( String canonicalResourcePath, Class resourceClass );



    // here it is, and you have full perms
    // here it is, and you have read only perms

    // does not exist
    // it is remote

}
