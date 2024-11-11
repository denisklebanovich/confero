import React from 'react';
import SessionTimeSetter from "@/components/admin-session/SessionTimeSetter.tsx";
import {Button} from "@/components/ui/button.tsx";
import {useNavigate} from "react-router-dom";

const MySessionsView = () => {
    const navigate = useNavigate();
    const sessions = [
        {
            "id": 1,
            "title": "Test Session",
            "tags": [
                "AI",
                "ML",
                "Facial Recognition",
                "Deep Learning",
                "Computer Vision"
            ],
            "startTime": "2024-11-10T15:47:25.313459200Z",
            "endTime": "2024-11-10T16:47:25.313459200Z",
            "presenters": [
                {
                    "id": 1,
                    "name": "Dinh-Hien",
                    "surname": "Nguyen",
                    "orcid": "0000-0002-0759-9656",
                    "isSpeaker": true
                },
                {
                    "id": 2,
                    "name": "Huyen",
                    "surname": "Nguyen",
                    "orcid": "0000-0002-0759-9656",
                    "isSpeaker": true,
                },
                {
                    "id": 3,
                    "name": "Huyen",
                    "surname": "Trang Phan",
                    "orcid": "0000-0002-0759-9656",
                    "isSpeaker": false
                },
                {
                    "id": 4,
                    "name": "Kang-Hyun",
                    "surname": " Jo",
                    "orcid": "0000-0002-0759-9656",
                    "isSpeaker": false
                }
                ]
        }
    ]



    return (
        <div className={"min-w-full min-h-full"}>
            <div className="flex w-full justify-center">
                <div className={"w-2/3 items-center gap-5 flex flex-col"}>
                    <div className="flex w-full flex-col">
                        <div className="text-3xl font-bold w-full flex pb-5">My Sessions:</div>
                        <SessionTimeSetter title={sessions[0].title} organisers={sessions[0].presenters} sessionID={sessions[0].id}/>
                        <SessionTimeSetter title={sessions[0].title} organisers={sessions[0].presenters} sessionID={sessions[0].id}/>
                        <SessionTimeSetter title={sessions[0].title} organisers={sessions[0].presenters} sessionID={sessions[0].id}/>
                        <SessionTimeSetter title={sessions[0].title} organisers={sessions[0].presenters} sessionID={sessions[0].id}/>
                        <div className="w-full flex justify-center">
                            <Button variant={"secondary_grey" as any} className={"mt-5"} onClick={() => navigate("/my-calendar")}>Back</Button>
                        </div>
                    </div>
                </div>
            </div>
        </div>


    );
};

export default MySessionsView;