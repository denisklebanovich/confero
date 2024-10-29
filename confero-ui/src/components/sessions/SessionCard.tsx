import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {CalendarIcon} from "lucide-react";
import {useNavigate} from "react-router-dom";
import {SessionPreviewResponse} from "@/generated";

const extractTime = (date: string) => {
    const d = new Date(date);
    return d.toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'});
}

const SessionCard = (session: SessionPreviewResponse) => {
    const navigate = useNavigate();


    return (
    <Card className="w-full cursor-pointer" onClick={() => navigate("/session")}>
        <CardHeader>
            <CardTitle className="text-xl font-bold">{session.title}</CardTitle>
        </CardHeader>
        <CardContent>
            <p className="text-sm text-muted-foreground mb-4">
                Topics: {session.tags?.map((topic) => topic).join(", ")}
            </p>
            <div className="flex items-center text-sm text-muted-foreground">
                <CalendarIcon className="mr-2 h-4 w-4" />
                <time>
                    {extractTime(session.startTime!!)} - {extractTime(session.endTime!!)}
                </time>
            </div>
        </CardContent>
    </Card>
    );
};

export default SessionCard;