import SessionCard from "@/components/sessions/SessionCard.tsx";
import {useApi} from "@/api/useApi.ts";
import {Button} from "@/components/ui/button.tsx";
import {useQueryClient} from "@tanstack/react-query";
import {CreateApplicationRequest, SessionPreviewResponse, SessionType} from "@/generated";

const SessionsView = () => {
    const {apiClient, useApiQuery, useApiMutation} = useApi();

    const queryClient = useQueryClient();

    const {data: sessions, isLoading} = useApiQuery<SessionPreviewResponse[]>(
        ['sessions'],
        () => apiClient.session.getSessions());

    const createSessionMutation = useApiMutation<SessionPreviewResponse, CreateApplicationRequest>(
        (request) => apiClient.session.createSession(request),
        {
            onSuccess: (newSession) => {
                queryClient.setQueryData<SessionPreviewResponse[]>(['sessions'],
                    (oldSessions) => [...(oldSessions || []), newSession]);
            }
        }
    )

    const handleCreateSession = () => {
        createSessionMutation.mutate({
            title: 'New Session',
            type: SessionType.SESSION,
            tags: [],
            description: 'New Session',
            presenters: [],
            saveAsDraft: false
        });
    }

    if (isLoading) {
        return <div>Loading...</div>
    }

    return (
        <div className={'min-w-full min-h-full'}>
            <Button className='mb-10' onClick={handleCreateSession}>
                Create test session
            </Button>
            <div className='flex w-full justify-center'>
                <div className={"w-2/3 items-center gap-5 flex flex-col"}>
                    <div className='text-3xl font-bold w-full'>Sessions:</div>
                    {sessions?.map((session) => (
                        <SessionCard key={session.id} {...session} />
                    ))}
                </div>
            </div>
        </div>
    );
};

export default SessionsView;