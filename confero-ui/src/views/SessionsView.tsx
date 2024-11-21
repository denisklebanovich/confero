import SessionCard from "@/components/sessions/SessionCard.tsx";
import {useApi} from "@/api/useApi.ts";
import {SessionPreviewResponse, SesssionEventResponse,} from "@/generated";
import {Button} from "@/components/ui/button";
import {useNavigate} from "react-router-dom";
import Event from "@/components/sessions/Event.tsx";
import {Spinner} from "@/components/ui/spiner.tsx";
import {useUser} from "@/state/UserContext.tsx";
import {useAuth} from "@/auth/AuthProvider.tsx";

const SessionsView = () => {
    const {apiClient, useApiQuery, useApiMutation} = useApi();
    const navigate = useNavigate();
    const {authorized} = useAuth();


    const {data: sessions, isLoading} = useApiQuery<SessionPreviewResponse[]>(
        ["sessions"],
        () => apiClient.session.getSessions()

    );

    const {data: events, isLoading: isLoadingEvents} = useApiQuery<SesssionEventResponse[]>(
        ["events"],
        () => {
            if(authorized) return apiClient.session.getSessionEvents()
        }
    );

    const {profileData, isLoading: isLoadingProfileData} = useUser();


    return (
        isLoading || isLoadingEvents ?
            <div className={"w-full flex justify-center mt-20"}>
                <Spinner/>
            </div>
            :
            <div className={"min-w-full min-h-full"}>

                <div className="flex w-full justify-around">
                    <div className={"w-1/5"}></div>
                    <div className={"w-2/3 items-center gap-5 flex flex-col"}>
                        <div className="flex w-full">
                            <div className="text-3xl font-bold w-full">All Sessions:</div>
                            {authorized  && !isLoadingProfileData && profileData.isInvitee &&
                                <Button onClick={() => navigate("/my-calendar")}>
                                    View my calendar
                                </Button>
                            }
                        </div>
                        {sessions?.map((session) => (
                            <SessionCard key={session.id} {...session} />
                        ))}
                    </div>
                    <div className={"w-1/5 flex flex-col justify-center pl-8 ml-5 mt-12"}>
                        {
                            profileData?.isInvitee && !isLoadingProfileData  && <>
                            {events.map(event => <Event
                                    key={event.id}
                                    sessionId={event.sessionId} userFirstName={event.userFirstName}
                                    userLastName={event.userLastName}/>)}</>
                        }

                    </div>
                </div>
            </div>
    );
};

export default SessionsView;
