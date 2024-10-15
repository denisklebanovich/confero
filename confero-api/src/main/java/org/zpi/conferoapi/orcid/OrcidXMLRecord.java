package org.zpi.conferoapi.orcid;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@JacksonXmlRootElement(localName = "record")
@Data
public class OrcidXMLRecord {
    @JacksonXmlProperty(localName = "given-names", namespace = "http://www.orcid.org/ns/personal-details")
    private String name;

    @JacksonXmlProperty(localName = "family-name", namespace = "http://www.orcid.org/ns/personal-details")
    private String surname;

    @JacksonXmlProperty(localName = "person")
    private OrcidPerson person;

    @Data
    @JacksonXmlRootElement(localName = "person")
    static class OrcidPerson {
        @JacksonXmlProperty(localName = "name")
        private OrcidName name;
    }

    @Data
    static class OrcidName {
        @JacksonXmlProperty(localName = "given-names")
        private String name;
        @JacksonXmlProperty(localName = "family-name")
        private String surname;
    }

    public String getName() {
        return person != null && person.getName() != null ? person.getName().getName() : null;
    }

    public String getSurname() {
        return person != null && person.getName() != null ? person.getName().getSurname() : null;
    }
}


