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
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.type.ChannelTypeUID;

/**
 * The {@link OchsnerWeb2ComBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Christian Becker - Initial contribution
 */
@NonNullByDefault
public class OchsnerWeb2ComBindingConstants {

    private static final String BINDING_ID = "ochsnerweb2com";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_WEB2COM = new ThingTypeUID(BINDING_ID, "web2com");
    public static final ThingTypeUID THING_TYPE_OCHSNERHEATERUNIT = new ThingTypeUID(BINDING_ID, "ochsner-heater-unit");
    public static final ThingTypeUID THING_TYPE_OCHSNERGENERIC = new ThingTypeUID(BINDING_ID, "ochsner-generic");

    // List of all Channel ids
    public static final ChannelTypeUID CHANNEL_TYPE_UID_STRING = new ChannelTypeUID(BINDING_ID, "testi");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_NUMBER = new ChannelTypeUID(BINDING_ID, "number");
}
