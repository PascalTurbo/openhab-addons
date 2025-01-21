package org.openhab.binding.ochsnerweb2com.internal.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class DataPointConfiguration {
    private Integer index;
    private String name;
    private String prop;
    private String desc;
    private Float value;
    private String unit;
    private Integer type;
    private Float step;
    private Float minValue;
    private Float maxValue;
}
