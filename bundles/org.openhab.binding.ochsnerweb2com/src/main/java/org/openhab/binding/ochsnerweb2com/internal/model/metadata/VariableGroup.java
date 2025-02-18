package org.openhab.binding.ochsnerweb2com.internal.model.metadata;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class VariableGroup {

    @XmlAttribute(name = "id")
    private Integer id;

    @XmlElement(name = "mn")
    private ArrayList<VariableGroupMember> variableGroupMembers;

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return the variableGroupMember
     */
    public VariableGroupMember getVariableGroupMemberById(Integer groupMemberId) {
        VariableGroupMember selectedMember = variableGroupMembers.stream()
                .filter(member -> groupMemberId.equals(member.getId())).findAny().orElse(null);

        return selectedMember;
    }
}
