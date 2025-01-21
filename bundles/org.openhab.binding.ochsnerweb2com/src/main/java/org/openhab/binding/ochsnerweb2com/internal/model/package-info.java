@XmlSchema(elementFormDefault = XmlNsForm.QUALIFIED, attributeFormDefault = XmlNsForm.QUALIFIED, xmlns = {
        @XmlNs(prefix = "soap", namespaceURI = "http://schemas.xmlsoap.org/soap/envelope/"),
        @XmlNs(prefix = "", namespaceURI = "http://ws01.lom.ch/soap/") })
package org.openhab.binding.ochsnerweb2com.internal.model;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
