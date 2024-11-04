import ApplicationCommentForm from "@/components/admin-application/ApplicationCommentForm";
import { Button } from "@/components/ui/button";
import { useNavigate } from "react-router-dom";
import {useState} from "react";

const CommentView = () => {
  const navigate = useNavigate();
  const [comment, setComment] = useState("");

  function onAddComment(e) {
    e.preventDefault();
    console.log("Comment added");
    navigate("/applications")
  }


  return (
    <div className="flex flex-col items-center justify-center">
      <div className="flex w-full justify-center">
        <ApplicationCommentForm value={comment} onChange={(e) => setComment(e.target.value)}/>
      </div>
      <div className="flex w-full max-w-md w-1/3 justify-between">
        <Button variant="secondary" onClick={() => navigate("/admin-proposal")}>Back</Button>
        <Button onClick={(e) => onAddComment(e)}>Send for adjustments</Button>
      </div>
    </div>
  );
};

export default CommentView;
