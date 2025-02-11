package org.openhab.binding.ochsnerweb2com.internal.model;

import javax.measure.Unit;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.library.unit.Units;
import org.openhab.core.types.State;

/*
 * Types
 * 
 * 
 *  - 2: Scalar Var
 * 
 *  - 7: level - has no value
 * 
 *  - 9: Enum Var
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

    public State getValueState() {

        // QuantityType(Number value, javax.measure.Unit<T> unit)
        // Creates a new QuantityType with the given value and Unit.
        // QuantityType.valueOf(1.23, unit)

        switch (type) {
            case 2, 9, 18 -> {
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
}
