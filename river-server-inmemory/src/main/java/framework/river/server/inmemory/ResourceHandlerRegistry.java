package framework.river.server.inmemory;

import com.mosaic.collections.ConsList;
import com.mosaic.lang.Validate;
import com.mosaic.lang.function.Function1;
import com.mosaic.lang.Nullable;


/**
 * Manages url mapping to resource handler classes.
 */
@SuppressWarnings("unchecked")
public class ResourceHandlerRegistry {

    private RegistryTree registry = new RootNode();



    public void addResource( String encodedURLRef, Class<?> resourceClass ) {
        ConsList<String> urlFragments = splitURL( encodedURLRef );

        registry = registry.addResource( urlFragments, resourceClass );
    }

    public void addAlias( String sourceEncodedURLRef, String destinationEncodedUrlRef ) {

    }

    public Nullable<DecodedResourceCall> decodeURL( String relativeURL ) {
        ConsList<String> urlFragments = splitURL( relativeURL );

        return registry.decode( urlFragments );
    }

    @SuppressWarnings("ManualArrayToCollectionCopy")
    private ConsList<String> splitURL( String encodedURLRef ) {
        Validate.isTrue( encodedURLRef.startsWith("/"), "relative url '%s' must start with '/'", encodedURLRef );

        String[]         splitString        = encodedURLRef.split("/");
        ConsList<String> urlFragmentsResult = ConsList.Nil;

        for ( int i=splitString.length-1; i>0; i-- ) {
            urlFragmentsResult.cons(splitString[i]);
        }

        return urlFragmentsResult;
    }




    private static RegistryTree createNodeFor( String urlFragmentTemplate ) {

        return new MatchStaticTextNode( urlFragmentTemplate );
    }




    private static abstract class RegistryTree {

        private ConsList<RegistryTree> children = ConsList.Nil;

        public abstract Nullable<DecodedResourceCall> decode( ConsList<String> urlFragments );


        protected Nullable<DecodedResourceCall> selectAndDecodeFirstMatchingChild(final ConsList<String> urlFragments) {
            return children.mapSingleValue(new Function1<Nullable<DecodedResourceCall>, RegistryTree>() {
                public Nullable<DecodedResourceCall> invoke(RegistryTree child) {
                    return child.decode(urlFragments);
                }
            });
        }

        public abstract boolean matches( String urlFragment );
        public abstract RegistryTree addResource( ConsList<String> urlFragmentTemplates, Class<?> resourceClass );



        protected void createAndAppendChildNode(ConsList<String> urlFragmentTemplates, Class<?> resourceClass) {
            String                 urlFragmentTemplate = urlFragmentTemplates.head();
            Nullable<RegistryTree> matchingChild       = findFirstMatchingChildFor(urlFragmentTemplate);

            if ( matchingChild.isNull() ) {
                RegistryTree newChild = createNodeFor( urlFragmentTemplate );

                children.cons(newChild);
            } else {
                matchingChild.getValue().addResource( urlFragmentTemplates.tail(), resourceClass );
            }
        }

        private Nullable<RegistryTree> findFirstMatchingChildFor(final String urlFragmentTemplate) {
            return children.fetchFirstMatch(new Function1<Boolean, RegistryTree>() {
                public Boolean invoke(RegistryTree child) {
                    return child.matches(urlFragmentTemplate);
                }
            });
        }

    }



    private static class RootNode extends RegistryTree {

        private Class rootResource;


        public Nullable<DecodedResourceCall> decode( final ConsList<String> urlFragments ) {
            return selectAndDecodeFirstMatchingChild(urlFragments);
        }

        public RegistryTree addResource( ConsList<String> urlFragmentTemplates, Class<?> resourceClass ) {
            if ( urlFragmentTemplates.isEmpty() ) {
                Validate.isNullState(rootResource, "rootResource", "A root resource has already been declared");

                rootResource = resourceClass;
            } else {
                createAndAppendChildNode( urlFragmentTemplates, resourceClass );
            }

            return this;
        }

        public boolean matches( String urlFragment ) {
            throw new UnsupportedOperationException("the RootNode cannot match a url fragment");
        }

    }

    private static class MatchStaticTextNode extends RegistryTree {

        private String targetText;


        public MatchStaticTextNode( String targetText ) {
            this.targetText = targetText;
        }

        public Nullable<DecodedResourceCall> decode( final ConsList<String> urlFragments ) {
            if ( urlFragments.isEmpty() || !targetText.equals(urlFragments.head()) ) {
                return Nullable.NULL;
            }

            return selectAndDecodeFirstMatchingChild(urlFragments.tail());
        }

        public RegistryTree addResource( ConsList<String> urlFragmentTemplates, Class<?> resourceClass ) {
            if ( urlFragmentTemplates.isEmpty() ) {
//                Validate.isNullState(rootResource, "rootResource", "A root resource has already been declared");
// TODO here
//                rootResource = resourceClass;
            } else {
                createAndAppendChildNode( urlFragmentTemplates, resourceClass );
            }

            return this;
        }

        public boolean matches( String urlFragment ) {
            return targetText.equals(urlFragment);
        }

    }

    private static class ExtractParameterNode extends RegistryTree {

        private String key;

        public Nullable<DecodedResourceCall> decode( final ConsList<String> urlFragments ) {
            return selectAndDecodeFirstMatchingChild(urlFragments.tail()).mapValue(new Function1<DecodedResourceCall, DecodedResourceCall>() {
                public DecodedResourceCall invoke(DecodedResourceCall result) {
                    result.appendParameter(key, urlFragments.head());

                    return result;
                }
            });
        }

        public RegistryTree addResource( ConsList<String> urlFragments, Class<?> resourceClass ) {
            return null;
        }

        public boolean matches( String urlFragment ) {
            return true;
        }

    }

    private static class ResourceMatchedNode extends RegistryTree {

        private Class resourceHandler;

        public Nullable<DecodedResourceCall> decode( final ConsList<String> urlFragments ) {
            return Nullable.createNullable(new DecodedResourceCall(resourceHandler));
        }

        public RegistryTree addResource( ConsList<String> urlFragments, Class<?> resourceClass ) {
            return null;
        }

        public boolean matches( String urlFragment ) {
            return false;
        }

    }

}
