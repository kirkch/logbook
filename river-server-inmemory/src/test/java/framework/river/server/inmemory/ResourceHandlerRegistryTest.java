package framework.river.server.inmemory;

import framework.river.lang.Nullable;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ResourceHandlerRegistryTest {

    private ResourceHandlerRegistry registry = new ResourceHandlerRegistry();


    @Test
    public void givenNoMappings_fetchURL_expectNull() {
        assertEquals( Nullable.NULL, registry.lookupResourceHandler("/accounts/abc") );
    }

    // givenMapping_fetchURLThatDoesNotMatch_expectNull
    // givenMapping_fetchURLThatDoesMatch_expectNull

}
