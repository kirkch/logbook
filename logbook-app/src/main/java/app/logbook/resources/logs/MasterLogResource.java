package app.logbook.resources.logs;

import framework.river.Message;
import framework.river.ResourceReference;
import framework.river.ResourceResponse;
import app.logbook.utils.CircularList;

import java.util.List;

/**
 *
 */
public class MasterLogResource {

    public static final ResourceReference REF = new ResourceReference(MasterLogResource.class);

    private CircularList<LogEntryDTO> log = new CircularList<LogEntryDTO>(100);


    public List<LogEntryDTO> get() {
        return log.toList();
    }


    public void delete() {
        log.clear();
    }


    public ResourceResponse handleMessage( LogEntryDTO newEntry ) {
        log.add(newEntry);

        return new ResourceResponse( Message.broadcast(newEntry, newEntry.isDroppableMessage) );
    }

}
