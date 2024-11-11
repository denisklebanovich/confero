import React, {useState} from 'react';
import {Button} from "@/components/ui/button.tsx";
import ConferenceEdition from "@/components/conference-editions/ConferenceEdition.tsx";
import EditionModal from "@/components/conference-editions/EditionModal.tsx";

const ConferenceEditionsView = () => {
    const mock = [
        {
            "id": 0,
            "applicationDeadlineTime": "2024-11-10T23:49:04.721Z",
            "numberOfInvitations": 0,
            "createdAt": "2024-11-10T23:49:04.721Z"
        },
        {
            "id": 1,
            "applicationDeadlineTime": "2024-11-15T17:00:00.000Z",
            "numberOfInvitations": 5,
            "createdAt": "2024-11-08T12:30:12.000Z"
        },
        {
            "id": 2,
            "applicationDeadlineTime": "2024-11-20T09:00:00.000Z",
            "numberOfInvitations": 3,
            "createdAt": "2024-11-09T15:22:33.000Z"
        },
        {
            "id": 3,
            "applicationDeadlineTime": "2024-12-01T00:00:00.000Z",
            "numberOfInvitations": 7,
            "createdAt": "2024-11-05T08:45:55.000Z"
        },
        {
            "id": 4,
            "applicationDeadlineTime": "2024-11-25T18:30:00.000Z",
            "numberOfInvitations": 2,
            "createdAt": "2024-11-03T11:05:43.000Z"
        }
    ]

    const [editions, setEditions] = useState(mock);

    const [open, setOpen] = useState(false);
    const [date, setDate] = useState("");
    const [editionIndex, setEditionIndex] = useState(-1);

    function addConferenceEdition() {
        setOpen(true);
    }

    function updateConferenceEdition(applicationDeadlineTime, id) {
        setDate(new Date(applicationDeadlineTime).toISOString().split("T")[0]);
        setEditionIndex(id);
        setOpen(true);
    }


    return (
        <div className={"min-w-full min-h-full"}>
            <div className="flex w-full justify-center">
                <div className={"w-2/3 items-center gap-5 flex flex-col"}>
                    <div className="flex w-full">
                        <div className="text-3xl font-bold w-full">Editions:</div>
                        <Button variant={"secondary_grey" as any} onClick={() => addConferenceEdition()}>
                            Add conference edition
                        </Button>
                    </div>
                    {editions.map(({id, applicationDeadlineTime}, index) =>
                            <ConferenceEdition key={index} index={index+1} id={id} applicationDeadlineTime={applicationDeadlineTime}  openModal={() => updateConferenceEdition(applicationDeadlineTime, index+1)}/>
                    )
                    }
                </div>
            </div>
            <EditionModal open={open} setOpen={setOpen}  index={editionIndex} setIndex={setEditionIndex} date={date} setDate={setDate}/>
        </div>
    );
};

export default ConferenceEditionsView;