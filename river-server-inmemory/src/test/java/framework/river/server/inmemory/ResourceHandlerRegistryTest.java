package framework.river.server.inmemory;

import com.mosaic.lang.Nullable;
import com.mosaic.utils.MapUtils;
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
        assertEquals( Nullable.NULL, registry.decodeURL("/users/abc") );
    }

    @Test
    public void givenMapping_decodeURLThatDoesNotMatch_expectNull() {
        registry.addResource( "/users/abc", UserResource.class );


        assertEquals( Nullable.NULL, registry.decodeURL("/users/foo") );
    }

    @Test
    public void givenMapping_decodeURLThatDoesMatch_expectDecodedRefWithNoParams() {
        registry.addResource( "/users/abc", UserResource.class );


        DecodedResourceCall expectedResult = new DecodedResourceCall("/users/abc", UserResource.class);

        assertEquals( expectedResult, registry.decodeURL("/users/abc").getValue() );
    }

    @Test
    public void givenNestedMapping_decodeSmallerURLThatDoesMatch_expectDecodedRefWithNoParams() {
        registry.addResource( "/users/abc", UserResource.class );
        registry.addResource( "/users",     UsersResource.class );


        DecodedResourceCall expectedResult = new DecodedResourceCall("/users", UsersResource.class);

        assertEquals( expectedResult, registry.decodeURL("/users").getValue() );
    }

    @Test
    public void givenNestedMapping_decodeLongerURLThatDoesMatch_expectDecodedRefWithNoParams() {
        registry.addResource( "/users/abc", UserResource.class );
        registry.addResource( "/users",     UsersResource.class );


        DecodedResourceCall expectedResult = new DecodedResourceCall("/users/abc", UserResource.class);

        assertEquals( expectedResult, registry.decodeURL("/users/abc").getValue() );
    }

    @Test
    public void givenNestedMappingsDeclaredInReverseOrder_decodeLongerURLThatDoesMatch_expectDecodedRefWithNoParams() {
        registry.addResource( "/users",     UsersResource.class );
        registry.addResource( "/users/abc", UserResource.class );


        DecodedResourceCall expectedResult = new DecodedResourceCall("/users/abc", UserResource.class);

        assertEquals( expectedResult, registry.decodeURL("/users/abc").getValue() );
    }

    @Test
    public void givenNestedMappingsDeclaredInReverseOrder_decodeSmallerURLThatDoesMatch_expectDecodedRefWithNoParams() {
        registry.addResource( "/users",     UsersResource.class );
        registry.addResource( "/users/abc", UserResource.class );


        DecodedResourceCall expectedResult = new DecodedResourceCall("/users/", UsersResource.class);

        assertEquals( expectedResult, registry.decodeURL("/users/").getValue() );
    }

    @Test
    public void givenNestedMapping_decodeLongerURLWithStraySlashThatDoesMatch_expectDecodedRefWithNoParams() {
        registry.addResource( "/users/abc", UserResource.class );
        registry.addResource( "/users",     UsersResource.class );


        DecodedResourceCall expectedResult = new DecodedResourceCall("/users/abc/", UserResource.class);

        assertEquals( expectedResult, registry.decodeURL("/users/abc/").getValue() );
    }

    @Test
    public void givenURLWithParam_decodeMatchingParam_expectParamToBeSuppliedInResult() {
        registry.addResource( "/users/$user_id", UserResource.class );


        Map<String,Object> expectedParams = MapUtils.asMap("user_id", "abc");
        DecodedResourceCall expectedResult = new DecodedResourceCall("/users/abc/", UserResource.class, expectedParams);

        assertEquals( expectedResult, registry.decodeURL("/users/abc/").getValue() );
    }

    // with params

    // givenMapping_decodeURLWithEscapedCharactersThatDoesMatch_expectDecodeToUnescapeCharacters

}
