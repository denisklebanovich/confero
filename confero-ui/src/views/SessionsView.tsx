import {apiInstance} from "@/service/api-instance.ts";
import {useQuery} from "@tanstack/react-query";
import SessionCard from "@/components/sessions/SessionCard.tsx";

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
        <div className={'min-w-full min-h-full'}>
            <div className='flex w-full justify-center'>
                <div className={"w-2/3 items-center gap-5 flex flex-col"}>
                    <div className='text-3xl font-bold w-full'>Sessions:</div>
                    <SessionCard/>
                    <SessionCard/>
                    <SessionCard/>
                    <SessionCard/>
                    <SessionCard/>
                    <SessionCard/>
                    <SessionCard/>
                </div>
            </div>
        </div>
    );
};

export default SessionsView;