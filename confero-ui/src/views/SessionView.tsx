import {useParams} from "react-router-dom";
import SessionDescription from "@/components/session/SessionDescription.tsx";
import Room from "@/components/session/Room.tsx";
import {useApi} from "@/api/useApi.ts";
import {useAuth} from "@/auth/AuthProvider.tsx";
import {Spinner} from "@/components/ui/spiner.tsx";
import PresentationCard from "@/components/presentations/PresentationCard.tsx";
import {useRef} from "react";
import {ChevronLeft, ChevronRight} from "lucide-react";
import {Button} from "@/components/ui/button.tsx";


export default function SessionView() {
    const {id} = useParams()
    const {apiClient, useApiQuery} = useApi()
    const scrollContainerRef = useRef<HTMLDivElement>(null)


    const scroll = (direction: "left" | "right") => {
        if (scrollContainerRef.current) {
            const scrollAmount = 400
            const newScrollLeft =
                scrollContainerRef.current.scrollLeft + (direction === "left" ? -scrollAmount : scrollAmount)
            scrollContainerRef.current.scrollTo({
                left: newScrollLeft,
                behavior: "smooth",
            })
        }
    }

    const {data: session, isLoading} = useApiQuery(
        ["session", id],
        () => apiClient.session.getSession(Number(id))
    )


    const {authorized} = useAuth()

    const getOrganisers = () => {
        return session.presentations.map(presentation => presentation.presenters).flat()
    }


    return (
        isLoading   ?
            <div className={"w-full flex justify-center mt-20"}>
                <Spinner/>
            </div>
            :
            <>

                <div className={`w-full flex items-center ${authorized ? "justify-around" : "justify-center"} mt-2`}>
                    {
                        authorized &&
                        <Room roomID={session.title.split(" ").join("-")} title={session.title}/>
                    }

                    <SessionDescription title={session.title} description={session.description}
                                        organisers={getOrganisers()}/>
                </div>
                {authorized ?
                    <div className='flex flex-col items-center w-full'>
                        {
                            session.presentations.map(presentation => (
                                <PresentationCard key={presentation.id} presentation={presentation}
                                                  sessionId={session.id}/>
                            ))
                        }
                    </div>
                    :
                    <div className="w-full mx-auto p-4 flex flex-row justify-around items-center">
                        <div className={"h-full w-16 flex align-top mr-5"}>
                            <Button
                                variant="outline"
                                size="icon"
                                className="h-10 w-10 rounded-full bg-background"
                                onClick={() => scroll("left")}
                            >
                                <ChevronLeft className="h-5 w-5"/>
                                <span className="sr-only">Scroll left</span>
                            </Button>
                        </div>
                        <div className="w-[92%]">
                            <div
                                ref={scrollContainerRef}
                                className="flex overflow-x-auto gap-4 scroll-smooth scrollbar-hide snap-x snap-mandatory"
                                style={{
                                    scrollbarWidth: "none",
                                    msOverflowStyle: "none",
                                }}>
                                {
                                    session.presentations.map(presentation => (
                                        <PresentationCard key={presentation.id} presentation={presentation}
                                                          sessionId={session.id}/>
                                    ))
                                }
                            </div>
                        </div>
                        <div className={"ml-5 h-full w-16"}>
                            <Button
                                variant="outline"
                                size="icon"
                                className="h-10 w-10 rounded-full bg-background"
                                onClick={() => scroll("right")}
                            >
                                <ChevronRight className="h-5 w-5"/>
                                <span className="sr-only">Scroll right</span>
                            </Button>
                        </div>
                    </div>
                }


            </>
    );
}
