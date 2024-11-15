import {useParams} from "react-router-dom";
import SessionDescription from "@/components/session/SessionDescription.tsx";
import Room from "@/components/session/Room.tsx";
import {useApi} from "@/api/useApi.ts";
import {useAuth} from "@/auth/AuthProvider.tsx";
import {Spinner} from "@/components/ui/spiner.tsx";
import PresentationCard from "@/components/presentations/PresentationCard.tsx";

export default function SessionView() {
    const {id} = useParams()
    const {apiClient, useApiQuery} = useApi()

    const {data: session, isLoading} = useApiQuery(
        ["session", id],
        () => apiClient.session.getSession(Number(id))
    )

    const {authorized} = useAuth()

    const getOrganisers = () => {
        return session.presentations.map(presentation => presentation.presenters).flat()
    }


    return (
        isLoading ?
            <div className={"w-full flex justify-center mt-20"}>
                <Spinner/>
            </div>
            :
            <>

                <div className={`w-full flex items-center ${authorized ? "justify-around" : "justify-center"} mt-2`}>
                    {
                        authorized &&
                        <Room roomID={session.title} title={session.title}/>
                    }

                    <SessionDescription title={session.title} description={session.description}
                                        organisers={getOrganisers()}/>
                </div>
                <div className='flex flex-col items-center w-full'>
                    {
                        session.presentations.map(presentation => (
                            <PresentationCard key={presentation.id} presentation={presentation} sessionId={session.id}/>
                        ))
                    }
                </div>
            </>
    );
}
