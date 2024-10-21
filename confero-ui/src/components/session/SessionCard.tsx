import {Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {SessionDto} from "@/generated";
import {CalendarDaysIcon} from "lucide-react";

const SessionCard = (session: SessionDto) => {
    return (
        <Card>
            <CardHeader>
                <CardTitle>{session.title}</CardTitle>
                <CardDescription>Card Description</CardDescription>
            </CardHeader>
            <CardContent>
                <p>Card Content</p>
            </CardContent>
            <CardFooter>
                <div className='flex flex-row items-center'>
                    <CalendarDaysIcon size={16}/>
                    <div className='ml-2'>{session.startTime}</div>
                    <div className='mx-2'>-</div>
                    <div>{session.endTime}</div>
                </div>
            </CardFooter>
        </Card>
    );
};

export default SessionCard;