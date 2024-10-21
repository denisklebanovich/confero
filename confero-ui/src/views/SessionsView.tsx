import {apiInstance} from "@/service/api-instance.ts";
import {useQuery} from "@tanstack/react-query";
import SessionCard from "@/components/session/SessionCard.tsx";

const SessionsView = () => {
    function useSessions() {
        return useQuery({
            queryKey: ['sessions'],
            queryFn: () => {
                return apiInstance.sessionController.getAllSessions();
            },
        });
    }

    const {data: sessions} = useSessions();

    return (
        <div>
            <div className='text-2xl font-bold'>Sessions:</div>
            <div className='flex items-center gap-2'>
                {sessions?.map((session) => (
                    <SessionCard key={session.id} {...session} />
                ))}
            </div>
        </div>
    );
};

export default SessionsView;