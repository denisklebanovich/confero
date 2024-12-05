import SessionCard from "@/components/sessions/SessionCard.tsx";
import {useApi} from "@/api/useApi.ts";
import {SessionPreviewResponse, SesssionEventResponse,} from "@/generated";
import {Button} from "@/components/ui/button";
import {useNavigate} from "react-router-dom";
import Event from "@/components/sessions/Event.tsx";
import {Spinner} from "@/components/ui/spiner.tsx";
import {useUser} from "@/state/UserContext.tsx";
import {useAuth} from "@/auth/AuthProvider.tsx";
import {DatePagination} from "@/components/sessions/DatePagination.tsx";
import {differenceInMilliseconds, endOfDay, isWithinInterval, parseISO, startOfDay} from 'date-fns'
import {useEffect, useState} from "react";

const findClosestSession = (sessions: SessionPreviewResponse[]) => {
    if (!sessions || sessions.length === 0) return null;

    return sessions.reduce((closest, session) => {
        if (!session.startTime) return closest;
        const sessionDate = parseISO(session.startTime);
        const closestDate = closest ? parseISO(closest.startTime) : null;

        return !closestDate ||
        Math.abs(differenceInMilliseconds(sessionDate, new Date())) <
        Math.abs(differenceInMilliseconds(closestDate, new Date()))
            ? session
            : closest;
    }, null);
};

const SessionsView = () => {
    const {apiClient, useApiQuery, useApiMutation} = useApi();
    const navigate = useNavigate();
    const {authorized} = useAuth()
    const [selectedDate, setSelectedDate] = useState<Date | null>(null);

    const {data: sessions, isLoading} = useApiQuery<SessionPreviewResponse[]>(
        ["sessions"],
        () => apiClient.session.getSessions()
    );

    const {data: mySessionIds, isLoading: isLoadingSessionIds} = useApiQuery<number[]>(
        ["mySessionsIds"],
        () => {if (authorized) return apiClient.session.getMySessions()}
    );

    const {data: events, isLoading: isLoadingEvents} = useApiQuery<SesssionEventResponse[]>(
        ["events"],
        () => {
            if (authorized) return apiClient.session.getSessionEvents()
        }
    );

    const {profileData, isLoading: isLoadingProfileData} = useUser();

    useEffect(() => {
        const closestSession = findClosestSession(sessions);
        if (closestSession?.startTime) {
            setSelectedDate(parseISO(closestSession.startTime));
        } else {
            setSelectedDate(new Date());
        }
    }, [sessions]);

    const filteredSessions = sessions?.filter((session) => {
        const sessionDate = session.startTime ? parseISO(session.startTime) : null;
        return (
            sessionDate &&
            isWithinInterval(sessionDate, {
                start: startOfDay(selectedDate || new Date()),
                end: endOfDay(selectedDate || new Date()),
            })
        );
    });

    function checkIfSessionIsMine(sessionId: number) {
        if (!mySessionIds) return false;
        return sessions.some(session => session.id === sessionId);
    }

    console.log('Sessions:', sessions);
    console.log('Filtered', filteredSessions);

    const handleDateChange = (date: Date) => {
        setSelectedDate(date)
    }

    return (
        isLoading || isLoadingEvents || isLoadingSessionIds ?
            <div className={"w-full flex justify-center mt-20"}>
                <Spinner/>
            </div>
            :
            <div className="min-w-full min-h-full">
                <div className="flex w-full justify-around">
                    <div className="w-1/5"></div>
                    <div className="w-2/3 items-center gap-5 flex flex-col">
                        <DatePagination
                            onDateChange={handleDateChange}
                            initialDate={selectedDate ? selectedDate.toISOString() : ""}
                        />
                        <div className="flex w-full">
                            <div className="text-3xl font-bold w-full">Sessions:
                            </div>
                            {authorized && !isLoadingProfileData && profileData.isInvitee && (
                                <Button onClick={() => navigate("/my-calendar")}>
                                    View my calendar
                                </Button>
                            )}
                        </div>
                        {filteredSessions?.map((session) => (
                            <SessionCard key={session.id} {...session} isMine={checkIfSessionIsMine(session.id)} />
                        ))}
                    </div>
                    <div className="w-1/5 flex flex-col justify-center pl-8 ml-5 mt-12">
                        {profileData?.isInvitee && !isLoadingProfileData && (
                            <>
                                {events?.map(event => (
                                    <Event
                                        key={event.id}
                                        sessionId={event.sessionId}
                                        userFirstName={event.userFirstName}
                                        userLastName={event.userLastName}
                                    />
                                ))}
                            </>
                        )}
                    </div>
                </div>
            </div>
    );
};

export default SessionsView;
