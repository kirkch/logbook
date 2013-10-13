package framework.river.server.inmemory;

import com.mosaic.lang.time.DTM;
import framework.river.*;
import com.mosaic.lang.Nullable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 */
public class RiverServerInmemoryTest {

    private static final long MAX_WAIT = 5000;

    private static final UserDTO JOHN  = new UserDTO("1", "John");
    private static final UserDTO SIMON = new UserDTO("2", "Simon");


    private RiverServer server = new RiverServerInmemory( new RiverSystemInmemory() );


    @Before
    public void setup() {
        server.start().spinUntilComplete(MAX_WAIT);
    }

    @After
    public void tearDown() {
        server.stop().spinUntilComplete(MAX_WAIT);
    }

// startStop

    // todo

// givenNoResourceRegisteredToURL

    @Test
    public void givenNoResourceRegisteredToURL_deliverGETRequest_expectNoResourceFoundResponse() {
        RiverResponse response = fetch( server.GET("/user/1") );

        assertNoResourceFoundFor(response);

        assertEquals( "no resource handler registered for '/user/1'", response.getDiagnosticMessageNbl().getValueNbl() );
    }

    @Test
    public void givenNoResourceRegisteredToURL_deliverPUTRequest_expectNoResourceFoundResponse() {
        assertNoResourceFoundFor( server.PUT("/user/1", Nullable.createNullable(JOHN)) );
    }

    @Test
    public void givenNoResourceRegisteredToURL_deliverPOSTRequest_expectNoResourceFoundResponse() {
        assertNoResourceFoundFor( server.POST("/user/1", Nullable.createNullable(JOHN)) );
    }

    @Test
    public void givenNoResourceRegisteredToURL_deliverPATCHRequest_expectNoResourceFoundResponse() {
        assertNoResourceFoundFor( server.PATCH("/user/1", Nullable.createNullable(JOHN)) );
    }

    @Test
    public void givenNoResourceRegisteredToURL_deliverHEADRequest_expectNoResourceFoundResponse() {
        assertNoResourceFoundFor( server.HEAD("/user/1") );
    }

    @Test
    public void givenNoResourceRegisteredToURL_deliverDELETERequest_expectNoResourceFoundResponse() {
        assertNoResourceFoundFor( server.DELETE("/user/1") );
    }

    @Test
    public void givenNoResourceRegisteredToURL_deliverSUBSCRIBERequest_expectNoResourceFoundResponse() {
        assertNoResourceFoundFor( server.SUBSCRIBE("/user/1") );
    }

    @Test
    public void givenNoResourceRegisteredToURL_deliverUNSUBSCRIBERequest_expectNoResourceFoundResponse() {
        assertNoResourceFoundFor( server.UNSUBSCRIBE("/user/1") );
    }




// givenUnInstantiatedResourceAtURL

//    @Test
    public void givenUnInstantiatedResourceAtURL_deliverGETRequest_expectNoResourceFoundResponse() {
        server.addResource( "/user/1", UserResource.class );

        RiverResponse response = fetch( server.GET("/user/1") );

        assertNoResourceFoundFor(response);
        assertEquals( "resource handler found for '/user/1', however no data was available", response.getDiagnosticMessageNbl().getValueNbl() );
    }


    //
    // givenUnInstantiatedResourceAtURL_deliverHEADRequest_expectNoResourceFoundResponse
    // givenUnInstantiatedResourceAtURL_deliverDELETERequest_expectNoResourceFoundResponse
    // givenUnInstantiatedResourceAtURL_deliverSUBSCRIBERequest_expectNoResourceFoundResponse
    // givenUnInstantiatedResourceAtURL_deliverUNSUBSCRIBERequest_expectNoResourceFoundResponse


    // givenUnInstantiatedResourceAtURL_deliverPUTRequest_expectSuccessResponse
    // givenUnInstantiatedResourceAtURL_deliverPOSTRequest_expectSuccessResponse
    // givenUnInstantiatedResourceAtURL_deliverPATCHRequest_expectSuccessResponse

    // givenUnInstantiatedResourceAtURLWhichDoesNotHandlePUTRequests_deliverPUTRequest_expectUnsupportedRequest
    // givenUnInstantiatedResourceAtURLWhichDoesNotHandlePOSTRequests_deliverPOSTRequest_expectUnsupportedRequest
    // givenUnInstantiatedResourceAtURLWhichDoesNotHandlePATCHRequests_deliverPATCHRequest_expectUnsupportedRequest


// givenInstantiatedResourceAtURL

    // givenInstantiatedResourceAtURL_deliverGETRequest_expectResponse
    // givenInstantiatedResourceAtURL_deliverHEADRequest_expectResponse
    // givenInstantiatedResourceAtURL_deliverSUBSCRIBERequest_expectSUCCESS
    // givenInstantiatedResourceAtURL_deliverSUBSCRIBEThenUNSUBSCRIBERequest_expectSUCCESS
    // givenInstantiatedResourceAtURL_deliverUNSUBSCRIBERequest_expectSUCCESSButWasNotSubscribed
    // givenInstantiatedResourceAtURL_deliverDELETERequest_expectSuccess
    // givenInstantiatedResourceAtURL_deliverDELETEThenGETRequest_expectNoResourceFound




// async events

// indexes
// search
// map/reduce
// bulk update statements



    private RiverResponse fetch( RiverRequest req ) {
        return server.process(req).spinUntilComplete(MAX_WAIT).getResultNoBlock();
    }


    private void assertNoResourceFoundFor( RiverRequest req ) {
        RiverResponse response = fetch( req );

        assertNoResourceFoundFor(response);
    }

    private void assertNoResourceFoundFor(RiverResponse response) {
        final AtomicBoolean hasResultBeenProcessed = new AtomicBoolean(false);
        response.invoke( new RiverResponseCallback() {
            public void noResourceFound(String requestId, Nullable<String> userIdNbl, Nullable<String> userNameNbl, Nullable<String> diagnosticMessageNbl) {
                hasResultBeenProcessed.set(true);
            }

            public void remoteHost(String requestId, Nullable<String> userIdNbl, Nullable<String> userNameNbl, Nullable<String> diagnosticMessageNbl, String remoteHost, int remotePort) {
                fail("expected noResourceFound");
            }

            public void processed(String requestId, Nullable<String> userIdNbl, Nullable<String> userNameNbl, Nullable<String> diagnosticMessageNbl, int httpStatus, Nullable<Object> bodyNbl, long cacheForSeconds, Nullable<DTM> lastModifiedDtmNbl) {
                fail("expected noResourceFound");
            }
        });

        assertTrue( "noResourceFound was not called", hasResultBeenProcessed.get() );
    }

}
