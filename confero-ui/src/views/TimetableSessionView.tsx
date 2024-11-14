import {Button} from "@/components/ui/button.tsx";
import {useNavigate, useParams} from "react-router-dom";
import Timetable from "@/components/timetable/Timetable.tsx";
import {useState} from "react";

const TimetableSessionView = () => {
    const navigate = useNavigate();
    const params = useParams();
    const sessionID = params.sessionID;

    const mock = {
        "id": "abc123",
        "title": "Artificial Intelligence in Healthcare",
        "description": "A comprehensive overview of the latest advancements in AI and its applications in the healthcare sector.",
        "presentations": [
            {
                "id": "1gfdsgfds",
                "title": "AI-Driven Diagnostics: The Future of Healthcare",
                "description": "This presentation discusses the role of AI in revolutionizing diagnostics and improving patient outcomes.",
                "attachments": [
                    {
                        "id": "att456",
                        "name": "AI Healthcare Report",
                        "url": "https://example.com/report.pdf"
                    },
                    {
                        "id": "att457",
                        "name": "Diagnostic AI Algorithms",
                        "url": "https://example.com/algorithms.pdf"
                    }
                ],
                "presenters": [
                    {
                        "id": 1,
                        "name": "John",
                        "surname": "Doe",
                        "orcid": "0000-0002-1825-0097",
                        "isSpeaker": true
                    },
                    {
                        "id": 2,
                        "name": "Jane",
                        "surname": "Smith",
                        "orcid": "0000-0001-2345-6789",
                        "isSpeaker": false
                    }
                ],
                "startTime": "2024-11-10T14:28:09.508Z",
                "endTime": "2024-11-10T16:00:00.000Z",
                "isMine": true
            },
            {
                "id": "gdfdggdf",
                "title": "AI in Healthcare Data Analysis",
                "description": "Exploring the potential of AI in analyzing healthcare data to enhance decision-making.",
                "attachments": [
                    {
                        "id": "att458",
                        "name": "Healthcare Data Insights",
                        "url": "https://example.com/data.pdf"
                    }
                ],
                "presenters": [
                    {
                        "id": 3,
                        "name": "Michael",
                        "surname": "Johnson",
                        "orcid": "0000-0003-4567-8901",
                        "isSpeaker": true
                    },
                    {
                        "id": 4,
                        "name": "Emily",
                        "surname": "Davis",
                        "orcid": "0000-0004-5678-9012",
                        "isSpeaker": false
                    }
                ],
                "startTime": "2024-11-10T10:10:00.000Z",
                "endTime": "2024-11-10T12:40:00.000Z",
                "isMine": false
            }
        ],
        "tags": [
            "AI",
            "Healthcare",
            "Diagnostics",
            "Innovation",
            "Data Analysis"
        ]
    }

    const [presentations, setPresentations] = useState(transformPresentations())


    function transformPresentations() {
        return mock.presentations.map((presentation, index) => {
            return {
                id: index,
                internal_id: presentation.id,
                title: presentation.title,
                description: presentation.description,
                organisers_line: presentation.presenters.map(presenter => `${presenter.name} ${presenter.surname}`).join(", "),
                start_date: presentation.startTime,
                end_date: presentation.endTime,
                toShow: presentation.isMine
            }
        });
    }


    function onSave() {
        navigate("/my-sessions")
    }


    return (
        <div className='flex flex-col items-center'>
            <div className='text-3xl font-bold'>Timetable of session</div>
            <div className='text-xl font-semibold pt-2'>{mock.title}</div>
            <Timetable presentations={presentations} setPresentations={setPresentations}/>
            <div className='w-full fixed bottom-16 left-0 pb-3 flex flex-row justify-between items-center px-[20vw]'>
                <Button onClick={() => navigate("/my-sessions")} variant='secondary'>Back</Button>
                <Button onClick={() => onSave()}>Save</Button>
            </div>
        </div>
    );
};

export default TimetableSessionView;