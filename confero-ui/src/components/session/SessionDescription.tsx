import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {PresenterResponse} from "@/generated";
import {useAuth} from "@/auth/AuthProvider.tsx";
import {Organisers} from "@/utils/Organisers.tsx";

type SessionDescriptionProps = {
    title: string;
    description: string;
    organisers: PresenterResponse[];
}
const SessionDescription = ({title, description, organisers}: SessionDescriptionProps) => {

    const {authorized} = useAuth()

    return (
        <Card className={`${authorized ? "w-1/3 h-[300px]" : "w-4/5"} mb-8`}>
            <CardHeader>
                <CardTitle>{title}</CardTitle>
                <CardDescription>
                    {description}
                </CardDescription>
            </CardHeader>
            <CardContent>
                <h3 className="font-semibold">Organizers</h3>
                <Organisers organisers={organisers} chunkSize={authorized ?  3 : 10}/>
            </CardContent>
        </Card>
    );
};

export default SessionDescription;