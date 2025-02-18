/**
 * Copyright (c) 2010-2025 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.ochsnerweb2com.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.ochsnerweb2com.internal.model.metadata.VariableIdentificators;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link OchsnerWeb2ComHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Christian Becker - Initial contribution
 */
@NonNullByDefault
public class OchsnerWeb2ComBridgeHandler extends BaseBridgeHandler {

    private final Logger logger = LoggerFactory.getLogger(OchsnerWeb2ComBridgeHandler.class);

    private final OchsnerWeb2ComConnection connection;

    private VariableIdentificators variableIdentificators;

    public OchsnerWeb2ComBridgeHandler(Bridge bridge, HttpClient httpClient) {
        super(bridge);

        this.connection = new OchsnerWeb2ComConnection(this, httpClient);

        // TODO: Remove this hack after clarifying how to handle async background initialization
        this.variableIdentificators = new VariableIdentificators();
    }

    public OchsnerWeb2ComConnection getConnection() {
        return connection;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // if (CHANNEL_1.equals(channelUID.getId())) {
        // if (command instanceof RefreshType) {
        // // TODO: handle data refresh
        // }

        // // TODO: handle command

        // // Note: if communication with thing fails for some reason,
        // // indicate that by setting the status with detail information:
        // // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
        // // "Could not control device at IP address x.x.x.x");
        // }
    }

    @Override
    public void initialize() {
        logger.debug("Initializing Web2Com handler.");
        OchsnerWeb2ComConfiguration config = getConfigAs(OchsnerWeb2ComConfiguration.class);

        if (config.hostname == null || config.hostname.isEmpty()) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "No hostname configured");
            return;
        }

        if (config.username == null || config.username.isEmpty()) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "No username configured");
            return;
        }

        // TODO: Initialize the handler.
        // The framework requires you to return from this method quickly, i.e. any network access must be done in
        // the background initialization below.
        // Also, before leaving this method a thing status from one of ONLINE, OFFLINE or UNKNOWN must be set. This
        // might already be the real thing status in case you can decide it directly.
        // In case you can not decide the thing status directly (e.g. for long running connection handshake using WAN
        // access or similar) you should set status UNKNOWN here and then decide the real status asynchronously in the
        // background.

        // set the thing status to UNKNOWN temporarily and let the background task decide for the real status.
        // the framework is then able to reuse the resources from the thing handler initialization.
        // we set this upfront to reliably check status updates in unit tests.
        updateStatus(ThingStatus.UNKNOWN);

        // Example for background initialization:
        // TODO: Correct background initialization
        // scheduler.execute(() -> {
        // TODO Bridge should hold its oid as a configuration parameter
        connection.testBridgeConnection("/1");

        // TODO Update if language changes
        variableIdentificators = connection.getVariableIdentificators();
        // });

        // These logging types should be primarily used by bindings
        // logger.trace("Example trace message");
        // logger.debug("Example debug message");
        // logger.warn("Example warn message");
        //
        // Logging to INFO should be avoided normally.
        // See https://www.openhab.org/docs/developer/guidelines.html#f-logging

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");
    }

    public OchsnerWeb2ComConfiguration getConfiguration() {
        return getConfigAs(OchsnerWeb2ComConfiguration.class);
    }

    public void setStatusInfo(ThingStatus status, ThingStatusDetail statusDetail, String description) {
        updateStatus(status, statusDetail, description);
    }

    // TODO: variableIdentificatores are initialized in Background.
    // but they are not nullable. How to check, if they are set?
    public VariableIdentificators getVariableIdentificators() {
        return variableIdentificators;
    }

    @Override
    public void dispose() {
        this.connection.dispose();
        super.dispose();
    }
}
