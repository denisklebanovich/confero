import {Button} from "@/components/ui/button.tsx";
import {useNavigate, useParams} from "react-router-dom";
import Timetable from "@/components/timetable/Timetable.tsx";
import {useEffect, useState} from "react";
import {useApi} from "@/api/useApi.ts";
import {Spinner} from "@/components/ui/spiner.tsx";
import {ApiError, SessionResponse, UpdatePresentationRequest, UpdateSessionRequest} from "@/generated";
import {useToast} from "@/hooks/use-toast.ts";
import {useQueryClient} from "@tanstack/react-query";

const TimetableSessionView = () => {
    const navigate = useNavigate();
    const {id} = useParams();
    const {apiClient, useApiQuery, useApiMutation} = useApi();
    const [calendarDate, setCalendarDate] = useState(new Date());
    const {toast} = useToast()
    const queryClient = useQueryClient()

    const {data: session, isLoading} = useApiQuery(
        ["session", id],
        () => apiClient.session.getSession(Number(id)),
    )


    const [presentations, setPresentations] = useState([]);

    const saveMutation = useApiMutation<SessionResponse, { sessionId: number; request: UpdateSessionRequest }>(
        ({sessionId, request}) => apiClient.session.updateSession(sessionId, request),
        {
            onSuccess: (newSessions) => {
                queryClient.setQueryData<SessionResponse[]>(
                    ["sessions"],
                    (oldSessions) => {
                        if (!oldSessions) return [newSessions]
                        return [...oldSessions, newSessions]
                    }
                )
                toast({
                    title: "Success!",
                    description: "Session timetable updated",
                })
            },
            onError: (error: ApiError) => {
                toast({
                    title: "Error occurred",
                    description: "Failed to update session timetable",
                    variant: "destructive"
                })
            },
        }
    )


    function getPresentations() {
        const defaultStartHour = 9;
        const timeInterval = 45;
        let currentStartTime = new Date();
        currentStartTime.setHours(defaultStartHour, 0, 0, 0);

        return session.presentations.map((presentation, index) => {
            let startDate, endDate;

            if (!presentation.startTime || !presentation.endTime) {
                startDate = new Date(currentStartTime);
                endDate = new Date(currentStartTime);
                endDate.setMinutes(currentStartTime.getMinutes() + timeInterval);
                currentStartTime = new Date(endDate.getTime() + 15 * 60 * 1000);
            } else {
                startDate = new Date(presentation.startTime);
                endDate = new Date(presentation.endTime);
            }

            return {
                id: index,
                internal_id: presentation.id,
                title: presentation.title,
                description: presentation.description,
                organisers_line: presentation.presenters
                    .map(presenter => `${presenter.name} ${presenter.surname}`)
                    .join(", "),
                start_date: startDate,
                end_date: endDate,
                toShow: presentation.isMine
            };
        });
    }


    function onSave() {
        navigate("/my-sessions")
        const formattedPresentations =
            presentations.filter(presentations => presentations.toShow).map(presentation => {
            return {
                id: presentation.internal_id,
                title: presentation.title,
                description: presentation.description,
                startTime: presentation.start_date,
                endTime: presentation.end_date,
            } as UpdatePresentationRequest
        })

        const request : UpdateSessionRequest = {
            presentations: formattedPresentations
        }

        saveMutation.mutate({
            request: request,
            sessionId: Number(id)
        });
    }


    useEffect(() => {
        if (session) {
            setCalendarDate(new Date(session.presentations[0].startTime|| Date.now()));
            setPresentations(getPresentations());
        }
    }, [session]);


    return (
        isLoading ?
            <div className={"w-full flex justify-center mt-20"}>
                <Spinner/>
            </div>
            :
            <div className='flex flex-col items-center'>
                <div className='text-3xl font-bold'>Timetable of session</div>
                <div className='text-xl font-semibold pt-2'>{session.title}</div>
                <Timetable presentations={presentations} setPresentations={setPresentations} date={calendarDate}/>
                <div
                    className='w-full fixed bottom-16 left-0 pb-3 flex flex-row justify-between items-center px-[20vw]'>
                    <Button onClick={() => navigate("/my-sessions")} variant='secondary'>Back</Button>
                    <Button onClick={() => onSave()}>Save</Button>
                </div>
            </div>
    );
};

export default TimetableSessionView;