import { Card, CardContent } from "@/components/ui/card";

interface ApplicationCommentProps {
  user: string;
  comment: string;
}

const ApplicationComment = ({ user, comment }: ApplicationCommentProps) => {
  return (
    <Card className="w-64 shadow-lg">
      <CardContent className="pt-6">
        <div className="flex items-start space-x-4">
          <div className="flex-1">
            <h3 className="font-semibold">{user}</h3>
            <p className="text-sm text-gray-500">
              {comment}
            </p>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default ApplicationComment;
