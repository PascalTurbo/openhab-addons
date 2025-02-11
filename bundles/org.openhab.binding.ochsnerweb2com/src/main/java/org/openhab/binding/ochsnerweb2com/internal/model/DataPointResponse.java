package org.openhab.binding.ochsnerweb2com.internal.model;

import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class DataPointResponse implements Serializable {

    @XmlElement(name = "ref")
    private Reference reference;

    @XmlElement(name = "dpCfg")
    private ArrayList<DataPointConfiguration> dataPointConfigurations;

    /**
     * @return the dataPointConfigurations
     */
    public ArrayList<DataPointConfiguration> getDataPointConfigurations() {
        return dataPointConfigurations;
    }
}
