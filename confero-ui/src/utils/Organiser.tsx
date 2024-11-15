import React from 'react';
import {PresenterResponse} from "@/generated";
import {any} from "zod";

const Organiser = ({name, surname, orcid, isSpeaker}: PresenterResponse|any) => {
    return (
        <span className={`cursor-pointer ${isSpeaker ? 'font-light' : ''}`}
              onClick={() => window.open(`https://orcid.org/${orcid}`, "_blank", "noopener,noreferrer")}>
             {name} {surname}
        </span>
    );
};

export default Organiser;