import {Button} from "@/components/ui/button";
import {Card, CardHeader, CardTitle,} from "@/components/ui/card";
import {useNavigate} from "react-router-dom";
import {Organisers} from "@/utils/Organisers.tsx";
import {ManagableSessionPreviewResponse, SessionPreviewResponse} from "@/generated";

const SessionTimeSetter = ({id, title, presenters, has_user_configured_timetable}: ManagableSessionPreviewResponse) => {
    const navigate = useNavigate();
    return (
        <Card className="w-full m-2">
            <CardHeader>
                <div className={"w-full flex justify-between"}>
                    <CardTitle className="text-xl">
                        {title}
                    </CardTitle>

                    <div className="flex flex-col sm:flex-row items-center sm:space-y-0 sm:space-x-2">
                        {
                            has_user_configured_timetable ?
                                <Button
                                    className="w-full sm:w-auto mr-2"
                                    variant={"secondary_grey" as any}
                                    onClick={() => navigate(`/sessions/${id}/timetable`)}
                                >
                                    Update timetable
                                </Button>
                                :<Button
                                    className="w-full sm:w-auto mr-2"
                                    onClick={() => navigate(`/sessions/${id}/timetable`)}
                                >
                                    Set timetable
                                </Button>
                        }

                    </div>
                </div>
                <h3 className="font-semibold">Organizers:</h3>
                <Organisers organisers={presenters} chunkSize={5}/>
            </CardHeader>
        </Card>
    );
};

export default SessionTimeSetter;
