import {Button} from "@/components/ui/button.tsx";
import {useLocation, useNavigate} from "react-router-dom";
import Timetable from "@/components/timetable/Timetable.tsx";

const TimetableSessionView = () => {
    const navigate = useNavigate();
    const params = new URLSearchParams(location.search);
    const sessionID = params.get("sessionID");

    const mock = {
        "id": "event123",
        "title": "Annual Science Symposium",
        "description": "An event featuring a series of presentations on recent advancements in science and technology.",
        "presentations": [
            {
                "id": "presentation1",
                "title": "The Future of AI",
                "description": "A deep dive into artificial intelligence and its impact on various industries.",
                "attachments": [
                    {
                        "id": "attachment1",
                        "name": "AI_Slides.pdf",
                        "url": "https://example.com/attachments/AI_Slides.pdf"
                    }
                ],
                "presenters": [
                    {
                        "id": 1,
                        "name": "Alice",
                        "surname": "Johnson",
                        "orcid": "0000-0001-2345-6789",
                        "isSpeaker": true
                    },
                    {
                        "id": 2,
                        "name": "Peter",
                        "surname": "Johnson",
                        "orcid": "0000-0001-2345-6789",
                        "isSpeaker": true
                    }
                ],
                "startTime": "2024-11-10T09:00:00Z",
                "endTime": "2024-11-10T10:30:00Z"
            },
            {
                "id": "presentation2",
                "title": "Renewable Energy Innovations",
                "description": "Exploring the latest breakthroughs in renewable energy sources.",
                "attachments": [
                    {
                        "id": "attachment2",
                        "name": "Renewable_Energy_Slides.pdf",
                        "url": "https://example.com/attachments/Renewable_Energy_Slides.pdf"
                    }
                ],
                "presenters": [
                    {
                        "id": 3,
                        "name": "Bob",
                        "surname": "Smith",
                        "orcid": "0000-0002-9876-5432",
                        "isSpeaker": true
                    }
                ],
                "startTime": "2024-11-10T11:00:00Z",
                "endTime": "2024-11-10T12:30:00Z"
            }
        ],
        "tags": [
            "AI",
            "Renewable Energy",
            "Technology"
        ]
    }

    function onSave() {

        // navigate("/my-sessions")
    }



    return (
        <div className='flex flex-col items-center'>
            <div className='text-3xl font-bold'>Timetable of session</div>
            <div className='text-xl font-semibold pt-2'>{mock.title}</div>
            <Timetable/>
            <div className='w-full fixed bottom-16 left-0 pb-3 flex flex-row justify-between items-center px-[20vw]'>
                <Button onClick={() => navigate("/my-sessions")} variant='secondary'>Back</Button>
                <Button onClick={() => onSave()}>Save</Button>
            </div>
        </div>
    );
};

export default TimetableSessionView;