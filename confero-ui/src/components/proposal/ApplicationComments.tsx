import { CommentResponse } from "@/generated";
import { ScrollArea } from "@/components/ui/scrollarea.tsx";
import ApplicationComment from "@/components/admin-application/ApplicationComment.tsx";

interface ApplicationCommentsProps {
    comments: CommentResponse[];
}

export function ApplicationComments({ comments }: ApplicationCommentsProps) {
    return (
        <div className="fixed top-0 right-0 h-full w-80 bg-white border-l border-gray-300 shadow-lg z-50 flex flex-col">
            <h2 className="text-xl font-semibold p-4 border-b border-gray-200">
                Comments
            </h2>
            <ScrollArea className="flex-1 p-4 overflow-y-auto">
                <div className="space-y-4">
                    {comments.map((comment, index) => (
                        <ApplicationComment key={index} {...comment} />
                    ))}
                </div>
            </ScrollArea>
        </div>
    );
}
