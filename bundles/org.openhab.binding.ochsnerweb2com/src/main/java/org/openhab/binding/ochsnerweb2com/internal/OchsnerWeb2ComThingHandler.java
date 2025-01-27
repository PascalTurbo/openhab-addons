package org.openhab.binding.ochsnerweb2com.internal;

import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OchsnerWeb2ComThingHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(OchsnerWeb2ComBridgeHandler.class);

    public OchsnerWeb2ComThingHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        logger.debug("Initializing OchsnerWeb2Com Thing Handler");

        getBridgeHandler();

        updateStatus(ThingStatus.UNKNOWN);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.debug("Handle command for channel " + channelUID + " Comamnd: " + command);
    }

    private OchsnerWeb2ComBridgeHandler getBridgeHandler() {
        Bridge bridge = getBridge();
        if (bridge != null) {
            OchsnerWeb2ComBridgeHandler bridgeHandler = (OchsnerWeb2ComBridgeHandler) bridge.getHandler();
            if (bridgeHandler != null) {
                return bridgeHandler;
            }
        }
        throw new IllegalStateException("The bridge must not be null and must be initialized");
    }
}
