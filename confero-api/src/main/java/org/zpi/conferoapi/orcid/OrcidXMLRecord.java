package org.zpi.conferoapi.orcid;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.Optional;

@JacksonXmlRootElement(localName = "record")
@Data
public class OrcidXMLRecord {

    @JacksonXmlProperty(localName = "person")
    private OrcidPerson person;

    @JacksonXmlProperty(localName = "activities-summary")
    private ActivitiesSummary activitiesSummary;

    @Data
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

    @Data
    static class ActivitiesSummary {
        @JacksonXmlProperty(localName = "employments")
        private Employments employments;
    }

    @Data
    static class Employments {
        @JacksonXmlProperty(localName = "affiliation-group")
        private AffiliationGroup affiliationGroup;
    }

    @Data
    static class AffiliationGroup {
        @JacksonXmlProperty(localName = "employment-summary")
        private EmploymentSummary employmentSummary;
    }

    @Data
    static class EmploymentSummary {

        @JacksonXmlProperty(localName = "role-title")
        private String roleTitle;

        @JacksonXmlProperty(localName = "organization")
        private Organization organization;
    }

    @Data
    static class Organization {

        @JacksonXmlProperty(localName = "name", namespace = "http://www.orcid.org/ns/common")
        private String name;
    }

    public String getName() {
        return Optional.ofNullable(person)
                .map(OrcidPerson::getName)
                .map(OrcidName::getName)
                .orElse(null);
    }

    public String getSurname() {
        return Optional.ofNullable(person)
                .map(OrcidPerson::getName)
                .map(OrcidName::getSurname)
                .orElse(null);
    }

    public String getTitle() {
        return Optional.ofNullable(activitiesSummary)
                .map(ActivitiesSummary::getEmployments)
                .map(Employments::getAffiliationGroup)
                .map(AffiliationGroup::getEmploymentSummary)
                .map(EmploymentSummary::getRoleTitle)
                .orElse(null);
    }

    public String getOrganization() {
        return Optional.ofNullable(activitiesSummary)
                .map(ActivitiesSummary::getEmployments)
                .map(Employments::getAffiliationGroup)
                .map(AffiliationGroup::getEmploymentSummary)
                .map(EmploymentSummary::getOrganization)
                .map(Organization::getName)
                .orElse(null);
    }
}


