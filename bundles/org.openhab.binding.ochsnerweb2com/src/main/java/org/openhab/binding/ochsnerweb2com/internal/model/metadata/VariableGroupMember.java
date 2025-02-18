package org.openhab.binding.ochsnerweb2com.internal.model.metadata;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class VariableGroupMember {

    @XmlAttribute(name = "id")
    private Integer id;

    @XmlValue
    private String name;

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
}
