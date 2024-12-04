import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {PresenterResponse} from "@/generated";
import {useAuth} from "@/auth/AuthProvider.tsx";
import {Organisers} from "@/utils/Organisers.tsx";
import {useUser} from "@/state/UserContext.tsx";

type SessionDescriptionProps = {
    title: string;
    description: string;
    organisers: PresenterResponse[];
    location?: string;
}
const SessionDescription = ({title, description, organisers, location}: SessionDescriptionProps) => {

    const {authorized} = useAuth()
    const {profileData, isLoading: isLoadingProfile} = useUser();

    return (
        <Card className={`${authorized && !isLoadingProfile && (profileData.isAdmin || profileData.isInvitee) ? "w-1/3 h-[300px]" : "w-4/5"} mb-8`}>
            <CardHeader>
                <CardTitle>{title}</CardTitle>
                <CardDescription>
                    {description}
                </CardDescription>
            </CardHeader>
            <CardContent className="flex-col space-y-2">
                <div>
                    <h3 className="font-semibold">Organizers</h3>
                    <Organisers organisers={organisers} chunkSize={authorized ? 3 : 10}/>
                </div>
                <div>
                    <h3 className="font-semibold">Location</h3>
                    <p className="text-sm text-muted-foreground">{location ? location : "The location for this session has not been specified yet."}</p>
                </div>
            </CardContent>
        </Card>
    );
};

export default SessionDescription;