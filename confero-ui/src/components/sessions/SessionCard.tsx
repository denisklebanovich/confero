import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {SessionDto} from "@/generated";
import {CalendarIcon} from "lucide-react";
import {useNavigate} from "react-router-dom";

const SessionCard = (session: SessionDto) => {
    const navigate = useNavigate();
    return (
    <Card className="w-full cursor-pointer" onClick={() => navigate("/session")}>
        <CardHeader>
            <CardTitle className="text-xl font-bold">Computer Vision and Intelligent systems</CardTitle>
        </CardHeader>
        <CardContent>
            <p className="text-sm text-muted-foreground mb-4">
                Topics: ImageProcessing, ObjectDetection, FacialRecognition, 3DReconstruction, FeatureExtraction
            </p>
            <div className="flex items-center text-sm text-muted-foreground">
                <CalendarIcon className="mr-2 h-4 w-4" />
                <time>
                    13:00 - 17:30
                </time>
            </div>
        </CardContent>
    </Card>
    );
};

export default SessionCard;