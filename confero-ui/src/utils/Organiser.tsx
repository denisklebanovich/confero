import React from 'react';


const Organiser = ({ organiser} : any) => {

    const { name, surname, orcid, isSpeaker } = organiser;
    return (
        <span className={`cursor-pointer ${isSpeaker ? 'font-medium' : ''}`}
              onClick={() => window.open(`https://orcid.org/${orcid}`, "_blank", "noopener,noreferrer")}>
             {name} {surname}
        </span>
    );
};

export default Organiser;