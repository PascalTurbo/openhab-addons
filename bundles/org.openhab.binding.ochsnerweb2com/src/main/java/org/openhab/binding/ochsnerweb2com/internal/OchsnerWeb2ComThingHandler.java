package org.openhab.binding.ochsnerweb2com.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.openhab.binding.ochsnerweb2com.internal.model.DataPointConfiguration;
import org.openhab.binding.ochsnerweb2com.internal.model.DataPointResponse;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.thing.binding.builder.ChannelBuilder;
import org.openhab.core.thing.binding.builder.ThingBuilder;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OchsnerWeb2ComThingHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(OchsnerWeb2ComBridgeHandler.class);

    private ScheduledFuture<?> pollChannelStatusSchedule;

    public OchsnerWeb2ComThingHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        logger.debug("Initializing OchsnerWeb2Com Thing Handler");

        getBridgeHandler();

        updateStatus(ThingStatus.UNKNOWN);

        // Example for background initialization:
        scheduler.execute(() -> {
            // TODO Bridge should hold its oid as a configuration parameter
            // TODO Maybe set status to configuring? and update to online after searching for channels is completed
            // ToDO Replace hard coded oids with configuration parameters
            getBridgeHandler().getConnection().testConnection("/1/2/1", this);

            getBridgeHandler().getConnection().getDataPointResponse("/1/2/1");

            // TODO Think about a better name
            Map<String, DataPointConfiguration> channelData = getBridgeHandler().getConnection()
                    .getChannelConfigurations("/1/2/1");

            // TODO only updateThing once
            for (Map.Entry<String, DataPointConfiguration> channelEntry : channelData.entrySet()) {
                String channelId = channelEntry.getKey();
                DataPointConfiguration dataPointConfiguration = channelEntry.getValue();
                ChannelUID channelUID = new ChannelUID(getThing().getUID(), channelId.replace("/", "_"));

                Map.Entry<Integer, Integer> variableGroupIdentifier = dataPointConfiguration
                        .getVariableGroupIdentifier();
                String label = getBridgeHandler().getVariableIdentificators().getVariableIdentifcationString(
                        variableGroupIdentifier.getKey(), variableGroupIdentifier.getValue());

                if (super.thing.getChannel(channelUID) == null) {
                    ThingBuilder thingBuilder = editThing();
                    // TODO: AcceptedItemType muss je nach Channel unterschiedlich gesetzt werden
                    ChannelBuilder channelbuilder = ChannelBuilder.create(channelUID, "String")
                            .withType(dataPointConfiguration.getChannelTypeUID()).withLabel(label)
                            .withDescription("Some description");

                    // TODO: Check if it's nescessary to distinguish between different UOMs or if "Number" is enough
                    // if (channelTypeUID != null) {
                    // channelbuilder.withAcceptedItemType(channelTypeUID.getAsString());
                    // }

                    Channel channel = channelbuilder.build();

                    thingBuilder.withChannel(channel);
                    updateThing(thingBuilder.build());
                }
            }
            // logger.info("got some channelids: " + test);
        });

        // TODO use value from configuration
        pollChannelStatusSchedule = scheduler.scheduleWithFixedDelay(this::pollChannelStatus, 0, 60, TimeUnit.SECONDS);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.debug("Handle command for channel " + channelUID + " Comamnd: " + command);
    }

    @Override
    public void handleRemoval() {
        if (pollChannelStatusSchedule != null && !pollChannelStatusSchedule.isCancelled()) {
            pollChannelStatusSchedule.cancel(true);
            pollChannelStatusSchedule = null;
        }

        super.handleRemoval();
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

    public void setStatusInfo(ThingStatus status, ThingStatusDetail statusDetail, String description) {
        updateStatus(status, statusDetail, description);
    }

    private void pollChannelStatus() {
        logger.info("Will poll channel status");

        List<Channel> channels = thing.getChannels();

        for (Channel channel : channels) {
            if (isLinked(channel.getUID())) {
                logger.info("Channel is linked: " + channel.getUID().toString());
                String oid = getOidForChannel(channel);

                // TODO: Handle dpr == null (Eigentlich schon vorher bei der Connection das sauber handeln)
                DataPointResponse dpr = getBridgeHandler().getConnection().getDataPointResponse(oid);
                ArrayList<DataPointConfiguration> dpcs = dpr.getDataPointConfigurations();

                if (dpcs.isEmpty()) {
                    logger.error("No data received for oid '" + oid + "'");
                    return;
                }

                if (dpcs.size() > 1) {
                    logger.error("Multiple data fields received for oid '" + oid + "'");
                    return;
                }

                DataPointConfiguration dpc = dpcs.getFirst();

                logger.info("Get Value " + dpc.getValueState() + "for oid " + oid);

                updateState(channel.getUID(), dpc.getValueState());
            }
        }
    }

    private String getOidForChannel(Channel channel) {
        return channel.getUID().getId().replace("_", "/");
    }
}
