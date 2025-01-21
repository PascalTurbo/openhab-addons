package org.openhab.binding.ochsnerweb2com.internal.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Body {

    @XmlElement(name = "getDpResponse", namespace = "http://ws01.lom.ch/soap/")
    private DataPointResponse dataPointResponse;

    @XmlElement(name = "getDpRequest", namespace = "http://ws01.lom.ch/soap/")
    private DataPointRequest dataPointRequest;

    public DataPointResponse getDataPointResponse() {
        return dataPointResponse;
    }

    public void setDataPointResponse(DataPointResponse dataPointResponse) {
        this.dataPointResponse = dataPointResponse;
    }

    public DataPointRequest getDataPointRequest() {
        return dataPointRequest;
    }

    public void setDataPointRequest(DataPointRequest dataPointRequest) {
        this.dataPointRequest = dataPointRequest;
    }

    public Body() {
    }

    public Body(DataPointResponse dataPointResponse) {
        this.dataPointResponse = dataPointResponse;
    }

    public Body(DataPointRequest dataPointRequest) {
        this.dataPointRequest = dataPointRequest;
    }
}
