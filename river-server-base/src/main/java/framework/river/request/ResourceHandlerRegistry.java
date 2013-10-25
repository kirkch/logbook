package framework.river.request;

import com.mosaic.collections.ConsList;
import com.mosaic.io.StandardStringCodecs;
import com.mosaic.io.StringCodec;
import com.mosaic.lang.Failure;
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
        registerParameterCodec("int", StandardStringCodecs.INTEGER_CODEC);
        registerParameterCodec("long", StandardStringCodecs.LONG_CODEC);
        registerParameterCodec("float", StandardStringCodecs.FLOAT_CODEC);
        registerParameterCodec("double", StandardStringCodecs.DOUBLE_CODEC);
    }


    /**
     * Register the following REST resource hander with the specified URL.
     * The URLs are relative URLs starting from the root of the container.
     * Named parameters may be included in the URL using one of several
     * formats:
     *
     * <table>
     *     <tr><td>/users/index</td><td>Fixed url with no parameters</td></tr>
     *     <tr><td>/users/$user_id</td><td>Will match both /users/u1 and /users/u2</td></tr>
     *     <tr><td>/users/${user_id}</td><td>Will match both /users/u1 and /users/u2</td></tr>
     *     <tr><td>/users/${user_id:long}</td><td>Will match /users/123 and fail with an error for /users/u1</td></tr>
     * </table>
     *
     *
     * @param encodedURLRef parameter encoded url
     * @param resourceClass the class that will handle the PUT/POST/GET/etc requests
     */
    public void addResource( String encodedURLRef, Class<?> resourceClass ) {
        ConsList<String> urlFragments = splitURL( encodedURLRef );

        registry.addResource( urlFragments, resourceClass, codecs );
    }

    /**
     * Add support for a new type.  Parameters within a URL can be marked
     * as having a type with the following format ${paramName:typeName}.  When
     * the url is matched and the param is extracted out it will be decoded
     * by the codec for the registered typeName.  Any errors will be reported.
     */
    public void registerParameterCodec( String typeName, StringCodec codec ) {
        codecs.put( typeName, codec );
    }


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
            return createExtractParameterNodeFromCurlyBracedDescription(urlFragmentTemplate, codecs);
        } else if ( urlFragmentTemplate.startsWith("$") ) {
            return new ExtractParameterNode( urlFragmentTemplate.substring(1) );
        } else {
            return new MatchStaticTextNode( urlFragmentTemplate );
        }
    }


    private static RegistryTree createExtractParameterNodeFromCurlyBracedDescription(String urlFragmentTemplate, Map<String, StringCodec> codecs) {
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
                            new Function1<DecodedResourceCall, TryNbl<DecodedResourceCall>>() {
                                public TryNbl<DecodedResourceCall> invoke(DecodedResourceCall v) {
                                    return candidateNode.decorateResourceCallResult(v, head);
                                }
                            }
                    ).getResultNoBlock();
                }
            };
        }

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


        /**
         * Invoked on matches to give the node a chance to modify the DecodedResourceCall result.
         */
        protected TryNbl<DecodedResourceCall> decorateResourceCallResult( DecodedResourceCall result, String urlFragment ) {
            return TryNow.successfulNbl(result);
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

    }

    private static class MatchStaticTextNode extends RegistryTree {

        private String targetText;


        public MatchStaticTextNode( String targetText ) {
            this.targetText = targetText;
        }


        public boolean matches( String urlFragment ) {
            return targetText.equals(urlFragment);
        }

    }

    private static class ExtractParameterNode extends RegistryTree {

        private String      key;
        private StringCodec codecNbl;

        public ExtractParameterNode( String paramName ) {
            this( paramName, StandardStringCodecs.NO_OP_CODEC );
        }

        public ExtractParameterNode( String paramName, StringCodec codecNbl) {
            this.codecNbl = codecNbl;
            this.key      = paramName.trim();

            Validate.notBlank( key, "paramName", "blank parameter names are not supported" );
        }


        public boolean matches( String urlFragment ) {
            return true;
        }

        protected TryNbl<DecodedResourceCall> decorateResourceCallResult( final DecodedResourceCall result, String urlFragment ) {
            Try decodedTry = codecNbl.decode(urlFragment);

            Try<DecodedResourceCall> updatedResultTry = decodedTry.mapResult(new Function1<Object, DecodedResourceCall>() {
                public DecodedResourceCall invoke(Object paramValue) {
                    result.appendParameter(key, paramValue);

                    return result;
                }
            }).recover(new Function1<Failure, DecodedResourceCall>() {
                public DecodedResourceCall invoke(Failure f) {
                    result.appendErrorMessage("Error decoding url parameter '" + key + "': " + f.getMessage());

                    return result;
                }
            });


            return updatedResultTry.toTryNbl();
        }

    }

}
