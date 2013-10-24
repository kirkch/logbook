package framework.river.server.inmemory;

import com.mosaic.collections.ConsList;
import com.mosaic.io.StandardStringCodecs;
import com.mosaic.io.StringCodec;
import com.mosaic.lang.Validate;
import com.mosaic.lang.functional.*;
import com.mosaic.utils.ListUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Manages url mapping to resource handler classes.
 */
@SuppressWarnings("unchecked")
public class ResourceHandlerRegistry {

    private RegistryTree            registry = new RootNode();
    private Map<String,StringCodec> codecs   = new HashMap();

    public ResourceHandlerRegistry() {
        codecs.put( "int",    StandardStringCodecs.INTEGER_CODEC );
        codecs.put( "long",   StandardStringCodecs.LONG_CODEC );
        codecs.put( "float",  StandardStringCodecs.FLOAT_CODEC );
        codecs.put( "double", StandardStringCodecs.DOUBLE_CODEC );
    }


    public void addResource( String encodedURLRef, Class<?> resourceClass ) {
        ConsList<String> urlFragments = splitURL( encodedURLRef );

        registry.addResource( urlFragments, resourceClass, codecs );
    }

//    public void addAlias( String sourceEncodedURLRef, String destinationEncodedUrlRef ) {
//
//    }


    // Future   -- value or exception or failure (in the future)
    // Nullable -- value or null

    // Try      -- value or exception or failure

    // FutureNbl
    // TryNbl

    /**
     * Locate a resource handler for the specified url.  Parameters will also be
     * extracted out of the url and decoded if appropriate with errors being
     * reported by the result.
     *
     * @param relativeURL after it has been url decoded eg '/users/chris kirk'
     */
    public TryNbl<DecodedResourceCall> matchURL( final String relativeURL ) {
        ConsList<String>            urlFragments = splitURL( relativeURL );
        TryNbl<DecodedResourceCall> resultNbl    = registry.matchURL(urlFragments);

        return finaliseResult( relativeURL, resultNbl );
    }


    private TryNbl<DecodedResourceCall> finaliseResult(final String relativeURL, TryNbl<DecodedResourceCall> resultNbl) {
        return resultNbl.mapResult( new Function1<DecodedResourceCall, DecodedResourceCall>() {
            public DecodedResourceCall invoke( DecodedResourceCall v ) {
                v.setRelativeURL(relativeURL);
                v.lock();

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




    private static RegistryTree createNodeFor( String urlFragmentTemplate, Map<String,StringCodec> codecs ) {
        urlFragmentTemplate = urlFragmentTemplate.trim();

        if ( urlFragmentTemplate.startsWith("${") ) {
            if ( !urlFragmentTemplate.endsWith("}") ) {
                throw new IllegalArgumentException("'"+urlFragmentTemplate+"' needs a closing brace");
            }

            int lastIndex = urlFragmentTemplate.length() - 1;
            int splitAt   = urlFragmentTemplate.indexOf(':');

            if ( splitAt > 0 ) {
                String typeLabel = urlFragmentTemplate.substring(splitAt+1,lastIndex).trim();

                StringCodec codec = codecs.get(typeLabel);

                if ( codec == null ) {
                    throw new IllegalArgumentException("No codec found for type '"+typeLabel+"'; add one using registerCodec()");
                }

                return new ExtractParameterNode( urlFragmentTemplate.substring(2,splitAt), codec );
            } else {
                return new ExtractParameterNode( urlFragmentTemplate.substring(2, lastIndex) );
            }
        } else if ( urlFragmentTemplate.startsWith("$") ) {
            return new ExtractParameterNode( urlFragmentTemplate.substring(1) );
        } else {
            return new MatchStaticTextNode( urlFragmentTemplate );
        }
    }




    private static abstract class RegistryTree {

        private TryNbl<DecodedResourceCall> resourceHandler  = TryNow.NULL;
        private List<RegistryTree>          children         = new ArrayList();


        public TryNbl<DecodedResourceCall> matchURL( final ConsList<String> urlFragments ) {
            if ( urlFragments.isEmpty() ) {
                return createResult();
            }

            return depthFirstRecursiveScanForFirstUrlMatch( urlFragments );
        }

        private TryNbl<DecodedResourceCall> createResult() {
            return resourceHandler.mapResult(new Function1<DecodedResourceCall, DecodedResourceCall>() {
                public DecodedResourceCall invoke(DecodedResourceCall decodedResourceCall) {
                    return decodedResourceCall.copy();
                }
            });
        }


        private TryNbl<DecodedResourceCall> depthFirstRecursiveScanForFirstUrlMatch( final ConsList<String> urlFragments ) {
            String head = urlFragments.head();

            Nullable<DecodedResourceCall> matchedNodeNbl = ListUtils.matchAndMapFirstResult(
                    children,
                    recursiveMatchWhichDecoratesResultAsStackUnwinds(urlFragments, head)
            );

            return TryNow.successfulNbl( matchedNodeNbl );
        }

        private Function1<RegistryTree, Nullable<DecodedResourceCall>> recursiveMatchWhichDecoratesResultAsStackUnwinds(final ConsList<String> urlFragments, final String head) {
            return new Function1<RegistryTree,Nullable<DecodedResourceCall>>() {
                public Nullable<DecodedResourceCall> invoke( final RegistryTree candidateNode ) {
                    if ( !candidateNode.matches(head) ) {
                        return Nullable.NULL;
                    }


                    TryNbl<DecodedResourceCall> decodedResourceCallNbl = candidateNode.matchURL(urlFragments.tail());

                    return decodedResourceCallNbl.flatMapResult(
                            new Function1<DecodedResourceCall,TryNbl<DecodedResourceCall>>() {
                                public TryNbl<DecodedResourceCall> invoke( DecodedResourceCall v ) {
                                    return candidateNode.decorateResourceCallResult(v, head);
                                }
                            }
                    ).getResultNoBlock();
                }
            };
        }

        /**
         * Invoked on matches to give the node a chance to modify the DecodedResourceCall result.
         */
        protected abstract TryNbl<DecodedResourceCall> decorateResourceCallResult( DecodedResourceCall result, String urlFragment );

        public abstract boolean matches( String urlFragment );


        public void addResource( ConsList<String> urlFragmentTemplates, Class<?> resourceClass, Map<String,StringCodec> codecs ) {
            if ( urlFragmentTemplates.isEmpty() ) {
                // Store the resource on this node, if not already set
                if ( !resourceHandler.isNull() ) {
                    throw new IllegalStateException( "'resourceHandler' must be null but was " + resourceHandler.getResultNoBlock().getValueNbl().getResourceHandler().getName() + ": 'A resource handler has already been declared'" );
                }

                resourceHandler = TryNow.successfulNbl( new DecodedResourceCall(resourceClass).lock() );

                return;
            }

            // create a child node and then carry on the search for where to set the resourceClass recursively
            String                 urlFragmentTemplate = urlFragmentTemplates.head();
            Nullable<RegistryTree> matchingChild       = findFirstMatchingChildFor(urlFragmentTemplate);

            if ( matchingChild.isNull() ) {
                RegistryTree newChild = createNodeFor( urlFragmentTemplate, codecs );

                children.add(newChild);

                newChild.addResource( urlFragmentTemplates.tail(), resourceClass, codecs );
            } else {
                matchingChild.getValue().addResource(urlFragmentTemplates.tail(), resourceClass, codecs);
            }
        }



        private Nullable<RegistryTree> findFirstMatchingChildFor(final String urlFragmentTemplate) {
            return ListUtils.firstMatch(
                    children,
                    new Function1<RegistryTree,Boolean>() {
                        public Boolean invoke(RegistryTree child) {
                            return child.matches(urlFragmentTemplate);
                        }
                    }
            );
        }

    }



    private static class RootNode extends RegistryTree {

        public boolean matches( String urlFragment ) {
            throw new UnsupportedOperationException("the RootNode cannot match a url fragment");
        }

        protected TryNbl<DecodedResourceCall> decorateResourceCallResult( DecodedResourceCall result, String urlFragment ) {
            return TryNow.successfulNbl(result);
        }

    }

    private static class MatchStaticTextNode extends RegistryTree {

        private String targetText;


        public MatchStaticTextNode( String targetText ) {
            this.targetText = targetText;
        }


        public boolean matches( String urlFragment ) {
            return targetText.equals(urlFragment);
        }

        protected TryNbl<DecodedResourceCall> decorateResourceCallResult( DecodedResourceCall result, String urlFragment ) {
            return TryNow.successfulNbl(result);
        }

    }

    private static class ExtractParameterNode extends RegistryTree {

        private String      key;
        private StringCodec codecNbl;

        public ExtractParameterNode( String paramName ) {
            this( paramName, null );
        }

        public ExtractParameterNode( String paramName, StringCodec codecNbl) {
            this.codecNbl = codecNbl;
            this.key      = paramName.trim();

            Validate.notBlank( key, "paramName", "blank parameter names are not supported" );
        }


        public boolean matches( String urlFragment ) {
            return true;
        }

        protected TryNbl<DecodedResourceCall> decorateResourceCallResult( DecodedResourceCall result, String urlFragment ) {
            Object paramValue = urlFragment;

            if ( codecNbl != null ) {
                Try decodedTry = codecNbl.decode(urlFragment);

                if ( decodedTry.hasResult() ) {
                    paramValue = decodedTry.getResultNoBlock();

                    result.appendParameter( key, paramValue );
                } else {
                    result.appendErrorMessage( "Error decoding url parameter '"+key+"': "+decodedTry.getFailureNoBlock().getMessage() );
                }
            } else {
                result.appendParameter( key, paramValue );
            }
            return TryNow.successfulNbl(result);
        }

    }

}
