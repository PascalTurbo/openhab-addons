package org.openhab.binding.ochsnerweb2com.internal.model.metadata;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "VarIdentTexte")
public class VariableIdentificators {

    @XmlAttribute(name = "lang")
    private String language;

    @XmlElement(name = "gn")
    private ArrayList<VariableGroup> variableGroups;

    public String getVariableIdentifcationString(Integer variableGroupId, Integer variableGroupMemberId) {

        String defaultIdentificator = variableGroupId.toString() + ":" + variableGroupMemberId.toString();

        if (variableGroups == null) {
            return defaultIdentificator;
        }

        String variableIdentifcationString = variableGroups.stream()
                .filter(group -> variableGroupId.equals(group.getId())).findAny()
                .map(group -> group.getVariableGroupMemberById(variableGroupMemberId).getName())
                .orElse(defaultIdentificator);

        return variableIdentifcationString;
    }
}
