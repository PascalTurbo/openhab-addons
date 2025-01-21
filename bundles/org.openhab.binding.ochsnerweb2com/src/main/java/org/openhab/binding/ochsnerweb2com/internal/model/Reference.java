package org.openhab.binding.ochsnerweb2com.internal.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Reference {
    @XmlElement(name = "oid")
    private String oid;

    @XmlElement(name = "prop")
    private String prop;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }

    public Reference() {
    }

    public Reference(String oid, String prop) {
        this.oid = oid;
        this.prop = prop;
    }
}
