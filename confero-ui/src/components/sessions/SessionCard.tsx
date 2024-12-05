import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {CalendarIcon} from "lucide-react";
import {useNavigate} from "react-router-dom";
import {SessionPreviewResponse} from "@/generated";
import {Button} from "@/components/ui/button.tsx";
import React from "react";
import {useApi} from "@/api/useApi.ts";
import {useCalendarStatus} from "@/hooks/useCalendarStatus.ts";
import {useAuth} from "@/auth/AuthProvider.tsx";
import {useUser} from "@/state/UserContext.tsx";

const extractDate = (date: string) => {
    const d = new Date(date);
    return d.toLocaleDateString("en-GB", {day: "2-digit", month: "short", year: "numeric"});
}

const extractTime = (date: string) => {
    const d = new Date(date);
    return d.toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'});
}

const SessionCard = ({id,title, isInCalendar, tags, startTime, endTime, isMine}:any) => {
    const {authorized} = useAuth();
    const navigate = useNavigate();

    const {apiClient, useApiMutation} = useApi();

    const {changeCalendarStatus} = useCalendarStatus();
    const {profileData, isLoading: isLoadingProfile} = useUser();

    const {mutate: addToCalendar} = useApiMutation(
        (sessionId: number) => apiClient.session.addSessionToAgenda({sessionId}),
        {
            onSuccess: () => {
                changeCalendarStatus(id, true);
            }
        }
    );

    const {mutate: deleteFromCalendar} = useApiMutation(
        (sessionId: number) => apiClient.session.removeSessionFromAgenda({sessionId}),
        {
            onSuccess: () => {
                changeCalendarStatus(id, false);
            }
        }
    );


    return (
        <Card className={`w-full cursor-pointer ${isMine && "border-2"}`} onClick={() => navigate(`/session/${id}`)}>
            <CardHeader>
                <div className="flex justify-between items-start">
                    <CardTitle className="text-xl font-bold">{title}</CardTitle>
                    {!authorized || isLoadingProfile || !profileData.isInvitee ? <></> : isInCalendar ? (
                        <Button
                            onClick={(e) => {
                                e.stopPropagation();
                                deleteFromCalendar(id);
                            }}
                            variant={"secondary_grey"}
                        >
                            Remove from my calendar
                        </Button>
                    ) : (
                        <Button
                            onClick={(e) => {
                                e.stopPropagation();
                                addToCalendar(id);
                            }}
                            variant={"secondary_grey"}
                        >
                            Add to my calendar
                        </Button>
                    )}
                </div>
            </CardHeader>
            <CardContent>
                <div className="h-full w-2/3">
                    <p className="text-sm text-muted-foreground mb-4">
                        Topics: {tags?.map((topic) => topic).join(", ")}
                    </p>
                    <div className="flex items-center text-sm text-muted-foreground">
                        <CalendarIcon className="mr-2 h-4 w-4"/>
                        <time>
                            {extractDate(startTime!)} | {extractTime(startTime!)} - {extractTime(endTime!)}
                        </time>
                    </div>
                </div>
            </CardContent>
        </Card>
    );
};

export default SessionCard;