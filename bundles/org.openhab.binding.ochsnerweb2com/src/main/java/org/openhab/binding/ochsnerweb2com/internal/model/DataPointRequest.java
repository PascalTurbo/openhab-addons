package org.openhab.binding.ochsnerweb2com.internal.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class DataPointRequest {

    @XmlElement(name = "ref", namespace = "http://ws01.lom.ch/soap/")
    private Reference reference;

    @XmlElement(name = "startIndex", namespace = "http://ws01.lom.ch/soap/")
    private Integer startIndex;

    @XmlElement(name = "count", namespace = "http://ws01.lom.ch/soap/")
    private Integer count;

    public Reference getReference() {
        return reference;
    }

    public void setReference(Reference reference) {
        this.reference = reference;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public DataPointRequest() {
    }

    public DataPointRequest(Reference reference, Integer startIndex, Integer count) {
        this.reference = reference;
        this.startIndex = startIndex;
        this.count = count;
    }
}
