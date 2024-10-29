import ApplicationCommentForm from "@/components/admin-application/ApplicationCommentForm";
import { Button } from "@/components/ui/button";
import { useNavigate } from "react-router-dom";

const CommentView = () => {
  const navigate = useNavigate();
  return (
    <div className="flex flex-col items-center justify-center">
      <div className="flex w-full justify-center">
        <ApplicationCommentForm />
      </div>
      <div className="flex w-full max-w-md w-1/3 justify-between">
        <Button variant="secondary" onClick={() => navigate("/admin-proposal")}>Back</Button>
        <Button>Send for adjustments</Button>
      </div>
    </div>
  );
};

export default CommentView;
