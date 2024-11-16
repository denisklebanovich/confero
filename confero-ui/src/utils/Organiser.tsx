import React from 'react';

interface OrganiserProps {
    organiser: {
        name: string;
        surname: string;
        orcid: string;
        isSpeaker: boolean;
    };
}

const Organiser = ({ organiser }: OrganiserProps) => {

    const { name, surname, orcid, isSpeaker } = organiser;
    return (
        <span className={`cursor-pointer ${isSpeaker ? 'font-light' : ''}`}
              onClick={() => window.open(`https://orcid.org/${orcid}`, "_blank", "noopener,noreferrer")}>
             {name} {surname}
        </span>
    );
};

export default Organiser;