import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {CalendarIcon} from "lucide-react";
import {useNavigate} from "react-router-dom";
import {SessionPreviewResponse} from "@/generated";
import {Button} from "@/components/ui/button.tsx";
import React, {useCallback, useMemo, useState} from "react";

const extractTime = (date: string) => {
    const d = new Date(date);
    return d.toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'});
}

const SessionCard = (session: SessionPreviewResponse) => {
    const navigate = useNavigate();

    const [isAdded, setIsAdded] = useState(false);
    const isLoggedIn = true;
    
    function onDeleteFromCalendar(e){
        e.stopPropagation();
        setIsAdded(false);
    }
    
    function onAddToCalendar(e){
        e.stopPropagation();
        setIsAdded(true);
    }
    

    function buttonNameAndFunction(){
        const path = location.pathname;

        if (path === "/my-calendar") {
            return { buttonName: "Delete", buttonFunction: (e) => onDeleteFromCalendar(e), variant: "danger"};
        }
        if(isAdded){
            return { buttonName: "Added", buttonFunction: (e) => onDeleteFromCalendar(e), variant: "secondary_grey"};
        }
        else {
            return { buttonName: "Add to my calendar", buttonFunction: (e) => onAddToCalendar(e), variant: "default"};
        }
        
    }

    // eslint-disable-next-line
    const buttonProps = useMemo(() => buttonNameAndFunction(),[isAdded, isLoggedIn]);


    return (
    <Card className="w-full cursor-pointer" onClick={() => navigate("/session")}>
        <CardHeader>
            <div className="flex justify-between items-start">
                <CardTitle className="text-xl font-bold">{session.title}</CardTitle>
                <Button onClick={buttonProps.buttonFunction} variant={buttonProps.variant as any}>{buttonProps.buttonName}</Button>
            </div>
        </CardHeader>
        <CardContent>

            <div className="h-full w-2/3">
            <p className="text-sm text-muted-foreground mb-4">
                Topics: {session.tags?.map((topic) => topic).join(", ")}
            </p>
            <div className="flex items-center text-sm text-muted-foreground">
                <CalendarIcon className="mr-2 h-4 w-4" />
                <time>
                    {extractTime(session.startTime!)} - {extractTime(session.endTime!)}
                </time>
            </div>
          </div>

        </CardContent>
    </Card>
    );
};

export default SessionCard;