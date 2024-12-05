import React from 'react';
import SessionTimeSetter from "@/components/admin-session/SessionTimeSetter.tsx";
import {useNavigate} from "react-router-dom";
import {useApi} from "@/api/useApi.ts";
import {
    ManagableSessionPreviewResponse,
} from "@/generated";

const MySessionsView = () => {
    const navigate = useNavigate();
    const {apiClient, useApiQuery} = useApi();

    const {data: sessions, isLoading} = useApiQuery<ManagableSessionPreviewResponse[]>(
        ["managable-sessions"],
        () => apiClient.session.getManagableSessions()
    );




    return (
        <div className={"min-w-full min-h-full"}>
            <div className="flex w-full justify-center">
                <div className={"w-2/3 items-center gap-5 flex flex-col"}>
                    <div className="flex w-full flex-col">
                        <div className="text-3xl font-bold w-full flex pb-5">My Sessions:</div>
                        {sessions?.map((session, index) => (
                            <SessionTimeSetter key={index} {...session}/>
                        ))}
                    </div>
                </div>
            </div>
        </div>


    );
};

export default MySessionsView;