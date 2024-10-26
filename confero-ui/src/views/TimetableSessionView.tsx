import {Button} from "@/components/ui/button.tsx";
import {useNavigate} from "react-router-dom";
import Timetable from "@/components/timetable/Timetable.tsx";

const TimetableSessionView = () => {
    const navigate = useNavigate();
    return (
        <div className='flex flex-col items-center'>
            <div className='text-3xl font-bold'>Timetable of session</div>
            <div className='text-xl font-semibold pt-2'>Intelligent and Contextual Systems</div>
            <Timetable/>
            <div className='w-full fixed bottom-14 left-0 pb-3 flex flex-row justify-between items-center px-[20vw]'>
                <Button onClick={()=>navigate("/admin-sessions")} variant='secondary'>Back</Button>
                <Button>Save</Button>
            </div>
        </div>
    );
};

export default TimetableSessionView;