import {Card, CardContent} from "@/components/ui/card";
import {CommentResponse} from "@/generated";
import {getFormattedDate} from "@/utils/dateUtils.ts";


const ApplicationComment = ({content, createdAt}: CommentResponse) => {
    return (
        <Card className="w-64 shadow-lg">
            <CardContent className="pt-6">
                <p className="text-sm text-gray-500">{content}</p>
                <p className="text-xs text-gray-400 mt-2">{getFormattedDate(createdAt)}</p>
            </CardContent>
        </Card>
    );
};

export default ApplicationComment;
