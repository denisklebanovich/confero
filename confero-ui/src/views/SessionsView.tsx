import SessionCard from "@/components/sessions/SessionCard.tsx";
import {useApi} from "@/api/useApi.ts";

const SessionsView = () => {
    const {apiClient, useApiQuery} = useApi();
    const {data: sessions, isLoading} = useApiQuery(['sessions'],
        () => apiClient.session.getSessions());

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