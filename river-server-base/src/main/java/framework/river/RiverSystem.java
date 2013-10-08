package framework.river;

import com.mosaic.lang.time.DTM;

/**
 *
 */
public interface RiverSystem {
    DTM currentDTM();

    String generateUUID();
}
