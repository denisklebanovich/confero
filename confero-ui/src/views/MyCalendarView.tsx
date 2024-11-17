import React from 'react';
import {Button} from "@/components/ui/button.tsx";
import SessionCard from "@/components/sessions/SessionCard.tsx";
import {useApi} from "@/api/useApi.ts";
import {useNavigate} from "react-router-dom";
import {SessionPreviewResponse} from "@/generated";
import {generateICSFile} from "@/utils/convertEventToISC.ts";
import {Spinner} from "@/components/ui/spiner.tsx";
import {useCalendarStatus} from "@/hooks/useCalendarStatus.ts";
import {useUser} from "@/state/UserContext.tsx";


const MyCalendarView = () => {
    const {apiClient, useApiQuery} = useApi();

    const navigate = useNavigate();

    const {data: sessions, isLoading} = useApiQuery<SessionPreviewResponse[]>(
        ["user-sessions"],
        () => apiClient.session.getPersonalAgenda()
    );

    const {profileData, isLoading: isLoadingUserData} = useUser();


    return (
        isLoading ?
            <div className={"w-full flex justify-center mt-20"}>
                <Spinner/>
            </div>
            :
            <div className={"min-w-full min-h-full"}>
                <div className="flex w-full justify-center">
                    <div className={"w-2/3 items-center gap-5 flex flex-col"}>
                        <div className="flex w-full">
                            <div className="text-3xl font-bold w-full">My calendar:</div>
                            {
                                !isLoadingUserData && !profileData.isAdmin &&
                            <Button onClick={() => navigate("/my-sessions")} variant={"secondary_grey" as any}>
                                Manage sessions
                            </Button>
                            }
                            <Button className={"ml-2"} onClick={() => navigate("/")}>
                                View full calendar
                            </Button>
                        </div>
                        {sessions?.map((session) => (
                            <SessionCard key={session.id} {...session} />
                        ))}
                        <div className="w-full text-sm text-gray-600 flex justify-start flex-col mt-5">
                            Export all of the sessions to the Google Calendar:
                            <Button onClick={() => generateICSFile(sessions)} variant={"secondary_grey" as any}
                                    className={"w-24 mt-2"}>
                                Export all
                            </Button>
                        </div>
                    </div>
                </div>
            </div>
    );
};

export default MyCalendarView;