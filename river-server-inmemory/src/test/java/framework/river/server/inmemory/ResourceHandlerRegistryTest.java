package framework.river.server.inmemory;


import com.mosaic.collections.ConsList;
import com.mosaic.lang.functional.Nullable;
import com.mosaic.lang.functional.TryNbl;
import com.mosaic.utils.MapUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class ResourceHandlerRegistryTest {

    private ResourceHandlerRegistry registry = new ResourceHandlerRegistry();


    @Test
    public void givenNoMappings_decodeURL_expectNull() {
        assertEquals( Nullable.NULL, registry.matchURL("/users/abc").getResultNoBlock() );
    }

    @Test
    public void givenMapping_decodeURLThatDoesNotMatch_expectNull() {
        registry.addResource( "/users/abc", UserResource.class );


        assertEquals( Nullable.NULL, registry.matchURL("/users/foo").getResultNoBlock() );
    }

    @Test
    public void givenMapping_decodeURLThatDoesMatch_expectDecodedRefWithNoParams() {
        registry.addResource( "/users/abc", UserResource.class );


        DecodedResourceCall expectedResult = new DecodedResourceCall("/users/abc", UserResource.class);

        TryNbl<DecodedResourceCall> decodedResourceCallTryNbl = registry.matchURL("/users/abc");
        Nullable<DecodedResourceCall> resultNoBlock = decodedResourceCallTryNbl.getResultNoBlock();
        assertEquals(expectedResult, resultNoBlock.getValue());
    }

    @Test
    public void givenNestedMapping_decodeSmallerURLThatDoesMatch_expectDecodedRefWithNoParams() {
        registry.addResource( "/users/abc", UserResource.class );
        registry.addResource( "/users",     UsersResource.class );


        DecodedResourceCall expectedResult = new DecodedResourceCall("/users", UsersResource.class);

        assertEquals( expectedResult, registry.matchURL("/users").getResultNoBlock().getValue() );
    }

    @Test
    public void givenNestedMapping_decodeLongerURLThatDoesMatch_expectDecodedRefWithNoParams() {
        registry.addResource( "/users/abc", UserResource.class );
        registry.addResource( "/users",     UsersResource.class );


        DecodedResourceCall expectedResult = new DecodedResourceCall("/users/abc", UserResource.class);

        assertEquals( expectedResult, registry.matchURL("/users/abc").getResultNoBlock().getValue() );
    }

    @Test
    public void givenNestedMappingsDeclaredInReverseOrder_decodeLongerURLThatDoesMatch_expectDecodedRefWithNoParams() {
        registry.addResource( "/users",     UsersResource.class );
        registry.addResource( "/users/abc", UserResource.class );


        DecodedResourceCall expectedResult = new DecodedResourceCall("/users/abc", UserResource.class);

        assertEquals( expectedResult, registry.matchURL("/users/abc").getResultNoBlock().getValue() );
    }

    @Test
    public void givenNestedMappingsDeclaredInReverseOrder_decodeSmallerURLThatDoesMatch_expectDecodedRefWithNoParams() {
        registry.addResource( "/users",     UsersResource.class );
        registry.addResource( "/users/abc", UserResource.class );


        DecodedResourceCall expectedResult = new DecodedResourceCall("/users/", UsersResource.class);

        assertEquals( expectedResult, registry.matchURL("/users/").getResultNoBlock().getValue() );
    }

    @Test
    public void givenNestedMapping_decodeLongerURLWithStraySlashThatDoesMatch_expectDecodedRefWithNoParams() {
        registry.addResource( "/users/abc", UserResource.class );
        registry.addResource( "/users",     UsersResource.class );


        DecodedResourceCall expectedResult = new DecodedResourceCall("/users/abc/", UserResource.class);

        assertEquals( expectedResult, registry.matchURL("/users/abc/").getResultNoBlock().getValue() );
    }

    @Test
    public void givenURLWithParam_decodeMatchingParam_expectParamToBeSuppliedInResult() {
        registry.addResource( "/users/$user_id", UserResource.class );


        Map<String,Object> expectedParams = MapUtils.asMap("user_id", "abc");
        DecodedResourceCall expectedResult = new DecodedResourceCall("/users/abc/", UserResource.class, expectedParams);

        assertEquals( expectedResult, registry.matchURL("/users/abc/").getResultNoBlock().getValue() );
    }

    @Test
    public void givenURLWithCurlyBracedParam_decodeMatchingParam_expectParamToBeSuppliedInResult() {
        registry.addResource( "/users/${user_id}", UserResource.class );


        Map<String,Object> expectedParams = MapUtils.asMap("user_id", "abc");
        DecodedResourceCall expectedResult = new DecodedResourceCall("/users/abc/", UserResource.class, expectedParams);

        assertEquals( expectedResult, registry.matchURL("/users/abc/").getResultNoBlock().getValue() );
    }

    @Test
    public void givenURLWithCurlyBracedParamContainingWhitespace_decodeMatchingParam_expectParamToBeSuppliedInResult() {
        registry.addResource( "/users/${ user_id }", UserResource.class );


        Map<String,Object> expectedParams = MapUtils.asMap("user_id", "abc");
        DecodedResourceCall expectedResult = new DecodedResourceCall("/users/abc/", UserResource.class, expectedParams);

        assertEquals( expectedResult, registry.matchURL("/users/abc/").getResultNoBlock().getValue() );
    }

    @Test
    public void givenURLWithSpaceBeforeAndAfterCurlyBracedParamContainingWhitespace_decodeMatchingParam_expectParamToBeSuppliedInResult() {
        registry.addResource( "/users/ ${user_id} ", UserResource.class );


        Map<String,Object> expectedParams = MapUtils.asMap("user_id", "abc");
        DecodedResourceCall expectedResult = new DecodedResourceCall("/users/abc/", UserResource.class, expectedParams);

        assertEquals( expectedResult, registry.matchURL("/users/abc/").getResultNoBlock().getValue() );
    }

    @Test
    public void givenURLWithMissingCurlyBracedParamName_expectIllegalArgumentException() {
        try {
            registry.addResource("/users/${  }", UserResource.class);

            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("blank parameter names are not supported", e.getMessage());
        }
    }

    @Test
    public void givenURLWithMissingCurlyBrace_expectIllegalArgumentException() {
        try {
            registry.addResource("/users/${name", UserResource.class);

            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("'${name' needs a closing brace", e.getMessage());
        }
    }

    @Test
    public void givenURLWithCurlyBracedParam_decodeMissingParam_expectNoMatch() {
        registry.addResource( "/users/${user_id}", UserResource.class );

        assertNull(registry.matchURL("/users").getResultNoBlock().getValueNbl());
    }

    @Test
    public void givenURLWithParams_decodeMatchingParam_expectParamToBeSuppliedInResult() {
        registry.addResource( "/users/${user_id}/audit/$audit_id", UserResource.class );


        Map<String,Object> expectedParams = MapUtils.asMap("user_id", "abc", "audit_id", "012");
        DecodedResourceCall expectedResult = new DecodedResourceCall("/users/abc/audit/012", UserResource.class, expectedParams);

        assertEquals(expectedResult, registry.matchURL("/users/abc/audit/012").getResultNoBlock().getValue());
    }

    @Test
    public void givenTwoUrlsWithParamsMostLongestDeclaredFirst_decodeShorterURL_expectMatch() {
        registry.addResource( "/users/${user_id}/audit/$audit_id", AuditResource.class );
        registry.addResource( "/users/${user_id}", UserResource.class );


        Map<String,Object> expectedParams = MapUtils.asMap("user_id", "abc");
        DecodedResourceCall expectedResult = new DecodedResourceCall("/users/abc/", UserResource.class, expectedParams);

        assertEquals( expectedResult, registry.matchURL("/users/abc/").getResultNoBlock().getValue() );
    }

    @Test
    public void givenTwoUrlsWithParamsMostShortestDeclaredFirst_decodeShorterURL_expectMatch() {
        registry.addResource( "/users/${user_id}", UserResource.class );
        registry.addResource( "/users/${user_id}/audit/$audit_id", AuditResource.class );


        Map<String,Object> expectedParams = MapUtils.asMap("user_id", "abc");
        DecodedResourceCall expectedResult = new DecodedResourceCall("/users/abc/", UserResource.class, expectedParams);

        assertEquals( expectedResult, registry.matchURL("/users/abc/").getResultNoBlock().getValue() );
    }

    @Test
    public void givenTwoClashingURLs_expect() {
        registry.addResource( "/users/${user_id}", UserResource.class );


        try {
            registry.addResource("/users/list", UsersResource.class);
            Assert.fail("expected IllegalStateException");
        } catch (IllegalStateException e) {
            Assert.assertEquals("'resourceHandler' must be null but was framework.river.server.inmemory.UserResource: 'A resource handler has already been declared'", e.getMessage());
        }
    }

    @Test
    public void givenTwoClashingURLs_expect2() {
        registry.addResource( "/users/list", UsersResource.class );
        registry.addResource( "/users/${user_id}", UserResource.class );


        DecodedResourceCall expectedResult = new DecodedResourceCall("/users/list/", UsersResource.class);

        assertEquals( expectedResult, registry.matchURL("/users/list/").getResultNoBlock().getValue() );
    }

    @Test
    public void givenURLWithUnrecognisedTypeParam_expectIAE() {
        try {
            registry.addResource("/users/${id:foo}", UserResource.class);

            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("No codec found for type 'foo'; add one using registerCodec()", e.getMessage());
        }
    }

    @Test
    public void givenURLWithIntTypeParam_matchURL_expectParamToBeDecoded() {
        registry.addResource( "/users/${user_id:int}", UserResource.class );


        Map<String,Object> expectedParams = MapUtils.asMap("user_id", 42);
        DecodedResourceCall expectedResult = new DecodedResourceCall("/users/42", UserResource.class, expectedParams);

        assertEquals( expectedResult, registry.matchURL("/users/42").getResultNoBlock().getValue() );
    }

    @Test
    public void givenURLWithIntTypeParam_matchURLWithInvalidInt_expectIllegalParamReport() {
        registry.addResource( "/users/${user_id:int}", UserResource.class );


        assertIllegalParam("/users/42a", UserResource.class, "Error decoding url parameter 'user_id': '42a' is not a valid number");
    }

    @Test
    public void givenURLWithMultipleIntTypeParams_matchURLWithInvalidInts_expectIllegalParamReports() {
        registry.addResource( "/users/${user_id:int}/transaction/${transaction_id:int}", UserResource.class );


        assertIllegalParam(
                "/users/42a/transaction/--22",
                UserResource.class,
                "Error decoding url parameter 'user_id': '42a' is not a valid number",
                "Error decoding url parameter 'transaction_id': '--22' is not a valid number"
        );
    }


    private void assertIllegalParam( String relativeUrl, Class<UserResource> userResourceClass, String...expectedDiagnosticMessages) {
        TryNbl<DecodedResourceCall> matchTryNbl = registry.matchURL( relativeUrl );

        assertTrue( matchTryNbl.hasResult() );

        DecodedResourceCall match = matchTryNbl.getResultNoBlock().getValue();

        assertEquals( relativeUrl, match.getRelativeURL() );
        assertEquals( userResourceClass, match.getResourceHandler() );
        assertTrue( match.hasErrored() );
        assertEquals( ConsList.newConsList(expectedDiagnosticMessages), match.getDiagnosticMessages() );
    }

}
