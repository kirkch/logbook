package framework.river.server.inmemory;

import com.mosaic.lang.Nullable;
import org.junit.Test;

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

//    @Test
    public void givenMapping_decodeURLThatDoesNotMatch_expectNull() {
        registry.addResource( "/users/abc", UserResource.class );


        DecodedResourceCall expectedResult = new DecodedResourceCall("/users/abc", UserResource.class);

        assertEquals( expectedResult, registry.decodeURL("/users/abc").getValue() );
    }

    //
    // givenMapping_decodeURLThatDoesMatch_expectDecodedRefWithNoParams
    // givenMapping_decodeURLThatDoesMatch_expectDecodedRefWithNoParams
    // givenMapping_decodeURLWithEscapedCharactersThatDoesMatch_expectDecodeToUnescapeCharacters



    // nested examples
}
