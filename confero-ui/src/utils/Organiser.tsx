import React from 'react';

const Organiser = ({ organiser}) => {
    return (
        <span className={`cursor-pointer ${organiser.isSpeaker ? 'font-medium' : ''}`} onClick={()=> window.open(`https://orcid.org/${organiser.orcid}`, "_blank", "noopener,noreferrer")}>
             {organiser.name} {organiser.surname}
        </span>
    );
};

export default Organiser;