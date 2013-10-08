package framework.river;



/**
 *
 */
public class Message {

    /**
     * A broadcast message has no destination and will only be delivered to those
     * who have subscribed to the resource.  If nobody has subscribed, then the
     * message will be dropped.
     */
    public static Message broadcast( Object payload, boolean isDroppableMessage ) {
        return new Message( null, payload, isDroppableMessage );
    }

    public Message( ResourceReference dest, Object payload ) {

    }

    public Message( ResourceReference dest, Object payload, boolean isDroppableMessage ) {

    }

}
