package framework.river.server.inmemory;


import com.mosaic.lang.functional.Nullable;
import com.mosaic.lang.functional.TryNbl;
import com.mosaic.utils.MapUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

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
    public void givenURLWithParams_decodeMatchingParam_expectParamToBeSuppliedInResult() {
        registry.addResource( "/users/${user_id}/audit/$audit_id", UserResource.class );


        Map<String,Object> expectedParams = MapUtils.asMap("user_id", "abc", "audit_id", "012");
        DecodedResourceCall expectedResult = new DecodedResourceCall("/users/abc/audit/012", UserResource.class, expectedParams);

        assertEquals( expectedResult, registry.matchURL("/users/abc/audit/012").getResultNoBlock().getValue() );
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

}
