import {Button} from "@/components/ui/button.tsx";
import {useNavigate, useParams} from "react-router-dom";
import Timetable from "@/components/timetable/Timetable.tsx";
import {useEffect, useState} from "react";
import {useApi} from "@/api/useApi.ts";
import {Spinner} from "@/components/ui/spiner.tsx";

const TimetableSessionView = () => {
    const navigate = useNavigate();
    const {id} = useParams();
    const {apiClient, useApiQuery} = useApi();

    const {data: session, isLoading} = useApiQuery(
        ["session", id],
        () => apiClient.session.getSession(Number(id)),
    )

    const [presentations, setPresentations] = useState([]);


    function getPresentations() {
        return session.presentations.map((presentation, index) => {
            return {
                id: index,
                internal_id: presentation.id,
                title: presentation.title,
                description: presentation.description,
                organisers_line: presentation.presenters.map(presenter => `${presenter.name} ${presenter.surname}`).join(", "),
                start_date: presentation.startTime,
                end_date: presentation.endTime,
            }
        });
    }


    function onSave() {
        navigate("/my-sessions")
    }

    useEffect(() => {
        if (session) {
            setPresentations(getPresentations());
        }
    }, []);


    return (
        isLoading ?
            <div className={"w-full flex justify-center mt-20"}>
                <Spinner/>
            </div>
            :
            <div className='flex flex-col items-center'>
                <div className='text-3xl font-bold'>Timetable of session</div>
                <div className='text-xl font-semibold pt-2'>{session.title}</div>
                <Timetable presentations={presentations} setPresentations={setPresentations}/>
                <div
                    className='w-full fixed bottom-16 left-0 pb-3 flex flex-row justify-between items-center px-[20vw]'>
                    <Button onClick={() => navigate("/my-sessions")} variant='secondary'>Back</Button>
                    <Button onClick={() => onSave()}>Save</Button>
                </div>
            </div>
    );
};

export default TimetableSessionView;