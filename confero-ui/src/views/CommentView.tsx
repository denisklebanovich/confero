import ApplicationComment from "@/components/admin-application/ApplicationComment";
import { Button } from "@/components/ui/button";

const CommentView = () => {
  return (
    <div className="flex flex-col items-center justify-center">
      <div className="flex w-full justify-center">
        <ApplicationComment />
      </div>
      <div className="flex w-full max-w-md w-1/3 justify-between">
        <Button variant="secondary">Back</Button>
        <Button>Send for adjustments</Button>
      </div>
    </div>
  );
};

export default CommentView;
