package org.openhab.binding.ochsnerweb2com.internal.model;

import static org.openhab.binding.ochsnerweb2com.internal.OchsnerWeb2ComBindingConstants.CHANNEL_TYPE_UID_NUMBER;
import static org.openhab.binding.ochsnerweb2com.internal.OchsnerWeb2ComBindingConstants.CHANNEL_TYPE_UID_STRING;

import java.util.AbstractMap;
import java.util.Map;

import javax.measure.Unit;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.library.unit.Units;
import org.openhab.core.thing.type.ChannelTypeUID;
import org.openhab.core.types.State;

/*
 * Types
 * 
 *  - -1: composite
 *  - 0: Scalar Var (API returns "--" for "Not set")
 *  - 2: Scalar Var
 *  - 4: Scalar Var (MWh)
 * 
 *  - 7: level - has no value
 * 
 *  - 9: Enum Var (API returns int values)
 *  - 13: Scalar Var (°C, kWh)
 *  - 18: Scalar Var (kWh)
 *  - 22: Scalar Var (%)
 *  - 29: SStrct Var (API returns int values, f.e. "Schaltzyklen, Betriebsstunden")
 *  - 30: LStrct Var
 * 
 */

// Examples

//  <index>0</index>
//  <name>05:04</name>
//  <prop>-rw-</prop>
//  <desc>Scalar Var</desc>
//  <value>60</value>
//  <unit>°C</unit>
//  <type>0</type>
//  <step>1</step>
//  <minValue>60</minValue>
//  <maxValue>80</maxValue>

// <prop>-r-- enum = 0,1,2,3,4,5,6,7,8,9,10,11</prop>
// <desc>Enum Var</desc>
// <value>11</value>
// <unit></unit>
// <type>9</type>
// <step>1</step>

// <index>0</index>
// <name>52:10</name>
// <prop>-r--</prop>
// <desc>Scalar Var</desc>
// <value>17.8</value>
// <unit>kWh</unit>
// <type>18</type>
// <step>0.1</step>
// <minValue></minValue>
// <maxValue></maxValue>

@XmlAccessorType(XmlAccessType.FIELD)
public class DataPointConfiguration {
    private Integer index;
    private String name;
    private String prop;
    private String desc;
    private String value;
    private String unit;
    private Integer type;
    private Float step;
    private Float minValue;
    private Float maxValue;

    /**
     * @return the index
     */
    public Integer getIndex() {
        return index;
    }

    /**
     * @return the type
     */
    public Integer getType() {
        return type;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    public Unit<?> getUnit() {
        if (unit.equals("kWh")) {
            return Units.KILOWATT_HOUR;
        }

        return null;
    }

    /*
     * Returns the value of the item with the corresponding type
     */
    public State getValueState() {

        // QuantityType(Number value, javax.measure.Unit<T> unit)
        // Creates a new QuantityType with the given value and Unit.
        // QuantityType.valueOf(1.23, unit)

        switch (type) {
            case 2, 9, 13, 18 -> {
                Unit<?> unit = getUnit();

                if (unit != null) {
                    return QuantityType.valueOf(Double.parseDouble(value), getUnit());
                }

                // if (unit.equals("°C")) {
                // return QuantityType.valueOf(Double.parseDouble(value), Units.);
                // }

                return DecimalType.valueOf(value);
            }
            case 30 -> {
                return StringType.valueOf(value);
            }
            default -> throw new AssertionError();
        }
    }

    /*
     * Returns the ChannelTypeUID based on the datapoint type
     */
    public ChannelTypeUID getChannelTypeUID() {
        switch (type) {
            case 0, 2, 4, 9, 13, 18, 22, 29 -> {
                return CHANNEL_TYPE_UID_NUMBER;
            }
            case 30 -> {
                return CHANNEL_TYPE_UID_STRING;
            }
            default ->
                throw new AssertionError("Unknown type '" + type + "', desc: '" + desc + "'+, unit: '" + unit + "'");
        }
    }

    /*
     * Returns the variableGroupId and the variableGroupMemberId
     * This is derived from the name field
     * f.e. 52:10 -> GroupId 52, GroupMemberId 10
     */
    // TODO: Better error handling and logging
    public Map.Entry<Integer, Integer> getVariableGroupIdentifier() {
        if (name.isEmpty()) {
            return null;
        }

        String[] ids = name.split(":");

        if (ids.length != 2) {
            return null;
        }

        return new AbstractMap.SimpleEntry<>(Integer.valueOf(ids[0]), Integer.valueOf(ids[1]));
    }
}
