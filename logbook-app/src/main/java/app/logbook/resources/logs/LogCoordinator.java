package app.logbook.resources.logs;


import framework.river.Message;
import framework.river.ResourceResponse;


/**
 * Receives log entries from clients one entry at a time as a POST command.
 */
public class LogCoordinator {

    private String serverId;
    private String appId;

    private String serverLogsURI;
    private String appLogsURI;



    public ResourceResponse post( LogEntryDTO newEntry ) {
        ResourceResponse response = new ResourceResponse();

        boolean isDroppableMessage = newEntry.isDroppableMessage;
        

        // new ResourceReference(AppLogResource.class, "appId", appId)

        response.addMessages(
                new Message(MasterLogResource.REF, newEntry, isDroppableMessage)
//                new Message("/apps/all/log", newEntry, isDroppableMessage),
//                new Message(serverLogsURI, newEntry, isDroppableMessage),
//                new Message(appLogsURI, newEntry, isDroppableMessage),
//
//                new Message("/requests/"+newEntry.requestId+"/log", newEntry, isDroppableMessage)
        );

        if ( newEntry.userId != null ) {
            response.addMessages(
//                    new Message("/users/"+newEntry.userId+"/log", newEntry, isDroppableMessage),
//                    new Message("/users/"+newEntry.userId+"/requests/"+newEntry.requestId, newEntry, isDroppableMessage)
            );
        }



        return response;
    }


    public void init() {
        serverLogsURI = "/servers/"+serverId+"/log";
        appLogsURI    = "/apps/"+appId+"/log";
    }

}
