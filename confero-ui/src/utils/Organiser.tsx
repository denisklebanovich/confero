import React from 'react';

const Organiser = ({index, organiser}) => {
    console.log(organiser)
    return (
        <span key={index} className={`cursor-pointer ${organiser.isSpeaker ? 'font-medium' : ''}`} onClick={()=> window.open(`https://orcid.org/${organiser.orcid}`, "_blank", "noopener,noreferrer")}>
             {organiser.name} {organiser.surname}
        </span>
    );
};

export default Organiser;