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

        registry.addResource( urlFragments, resourceClass );
    }

//    public void addAlias( String sourceEncodedURLRef, String destinationEncodedUrlRef ) {
//
//    }

    /**
     * Locate a resource handler for the specified url.  Parameters will also be
     * extracted out of the url and decoded if appropriate with errors being
     * reported by the result.
     *
     * @param relativeURL after it has been url decoded eg '/users/chris kirk'
     */
    public Nullable<DecodedResourceCall> matchURL( final String relativeURL ) {
        ConsList<String> urlFragments = splitURL( relativeURL );

        Nullable<DecodedResourceCall> resultNbl = registry.matchURL(urlFragments);

        return resultNbl.mapValue( new Function1<DecodedResourceCall, DecodedResourceCall>() {
            public DecodedResourceCall invoke( DecodedResourceCall v ) {
                v.setRelativeURL(relativeURL);

                return v;
            }
        });
    }

    @SuppressWarnings("ManualArrayToCollectionCopy")
    private ConsList<String> splitURL( String encodedURLRef ) {
        Validate.isTrue( encodedURLRef.startsWith("/"), "relative url '%s' must start with '/'", encodedURLRef );

        String[]         splitString        = encodedURLRef.split("/");
        ConsList<String> urlFragmentsResult = ConsList.Nil;

        for ( int i=splitString.length-1; i>0; i-- ) {
            urlFragmentsResult = urlFragmentsResult.cons(splitString[i]);
        }

        return urlFragmentsResult;
    }




    private static RegistryTree createNodeFor( String urlFragmentTemplate ) {
// todo
        // todo ${}
        // todo ${:}
        // todo blank names

        if ( urlFragmentTemplate.startsWith("${") && urlFragmentTemplate.endsWith("}")) {
            return new ExtractParameterNode( urlFragmentTemplate.substring(2,urlFragmentTemplate.length()-1) );
        } else if ( urlFragmentTemplate.startsWith("$") ) {
            return new ExtractParameterNode( urlFragmentTemplate.substring(1) );
        } else {
            return new MatchStaticTextNode( urlFragmentTemplate );
        }
    }




    private static abstract class RegistryTree {

        private Class resourceHandler;
        private ConsList<RegistryTree> children     = ConsList.Nil;


        public Nullable<DecodedResourceCall> matchURL( final ConsList<String> urlFragments ) {
            if ( urlFragments.isEmpty() ) {
                return resourceHandler == null ? Nullable.NULL : Nullable.createNullable(new DecodedResourceCall(resourceHandler));
            }

            return depthFirstRecursiveScanForFirstUrlMatch(urlFragments);
        }


        private Nullable<DecodedResourceCall> depthFirstRecursiveScanForFirstUrlMatch(final ConsList<String> urlFragments) {
            final String head = urlFragments.head();   // NB already asserted from matchURL as being safe

            return children.reverse().mapSingleValue(    // todo optimisation; don't call reverse each time.. we do it so that 'match' first semantics match the order that resources were added
                    new Function1<RegistryTree,Nullable<DecodedResourceCall>>() {
                        public Nullable<DecodedResourceCall> invoke( final RegistryTree child ) {
                            if ( !child.matches(head) ) {
                                return Nullable.NULL;
                            }

                            Nullable<DecodedResourceCall> decodedResourceCallNbl = child.matchURL(urlFragments.tail());

                            return decodedResourceCallNbl.mapValue(
                                    new Function1<DecodedResourceCall,DecodedResourceCall>() {
                                        public DecodedResourceCall invoke( DecodedResourceCall v ) {
                                            child.decorateResourceCallResult( v, head );

                                            return v;
                                        }
                                    }
                            );
                        }
                    }
            );
        }

        /**
         * Invoked on matches to give the node a chance to modify the DecodedResourceCall result.
         */
        protected abstract void decorateResourceCallResult( DecodedResourceCall result, String urlFragment );

        public abstract boolean matches( String urlFragment );


        public void addResource( ConsList<String> urlFragmentTemplates, Class<?> resourceClass ) {
            if ( urlFragmentTemplates.isEmpty() ) {
                // Store the resource on this node, if not already set
                Validate.isNullState(resourceHandler, "resourceHandler", "A resource handler has already been declared");

                resourceHandler = resourceClass;

                return;
            }

            // create a child node and then carry on the search for where to set the resourceClass recursively
            String                 urlFragmentTemplate = urlFragmentTemplates.head();
            Nullable<RegistryTree> matchingChild       = findFirstMatchingChildFor(urlFragmentTemplate);

            if ( matchingChild.isNull() ) {
                RegistryTree newChild = createNodeFor( urlFragmentTemplate );

                this.children = children.cons(newChild);

                newChild.addResource( urlFragmentTemplates.tail(), resourceClass );
            } else {
                matchingChild.getValue().addResource(urlFragmentTemplates.tail(), resourceClass);
            }
        }



        private Nullable<RegistryTree> findFirstMatchingChildFor(final String urlFragmentTemplate) {
            return children.fetchFirstMatch(new Function1<RegistryTree,Boolean>() {
                public Boolean invoke(RegistryTree child) {
                    return child.matches(urlFragmentTemplate);
                }
            });
        }

    }



    private static class RootNode extends RegistryTree {

        public boolean matches( String urlFragment ) {
            throw new UnsupportedOperationException("the RootNode cannot match a url fragment");
        }

        protected void decorateResourceCallResult( DecodedResourceCall result, String urlFragment ) {}
    }

    private static class MatchStaticTextNode extends RegistryTree {

        private String targetText;


        public MatchStaticTextNode( String targetText ) {
            this.targetText = targetText;
        }


        public boolean matches( String urlFragment ) {
            return targetText.equals(urlFragment);
        }

        protected void decorateResourceCallResult( DecodedResourceCall result, String urlFragment ) {}

    }

    private static class ExtractParameterNode extends RegistryTree {

        private String key;

        public ExtractParameterNode( String paramName ) {
            this.key = paramName;
        }


        public boolean matches( String urlFragment ) {
            return true;
        }

        protected void decorateResourceCallResult( DecodedResourceCall result, String urlFragment ) {
            result.appendParameter( key, urlFragment );
        }

    }

}
