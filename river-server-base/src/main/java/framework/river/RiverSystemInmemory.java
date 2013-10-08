package framework.river;

import com.mosaic.lang.time.DTM;
import com.mosaic.lang.time.SystemClock;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 */
public class RiverSystemInmemory implements RiverSystem {

    private final AtomicLong  uuidCounter = new AtomicLong(0L);
    private final SystemClock clock;


    public RiverSystemInmemory() {
        this( new SystemClock() );
    }

    public RiverSystemInmemory( DTM nowDTM ) {
        this( new SystemClock(nowDTM) );
    }

    public RiverSystemInmemory( SystemClock clock ) {
        this.clock = clock;
    }


    public DTM currentDTM() {
        return clock.getCurrentDTM();
    }

    public String generateUUID() {
        return Long.toString(uuidCounter.incrementAndGet());
    }

}
