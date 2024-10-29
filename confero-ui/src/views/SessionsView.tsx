import { apiInstance } from "@/service/api-instance.ts";
import { useQuery } from "@tanstack/react-query";
import SessionCard from "@/components/sessions/SessionCard.tsx";
import { Button } from "@/components/ui/button";
import { useNavigate } from "react-router-dom";

const SessionsView = () => {
  function useSessions() {
    return useQuery({
      queryKey: ["sessions"],
      queryFn: () => {
        return apiInstance.sessionController.getAllSessions();
      },
    });
  }

  const { data: sessions } = useSessions();
  const navigate = useNavigate();

  return (
    <div className={"min-w-full min-h-full"}>
      <div className="flex w-full justify-center">
        <div className={"w-2/3 items-center gap-5 flex flex-col"}>
          <div className="flex w-full">
            <div className="text-3xl font-bold w-full">Sessions:</div>
            <Button onClick={() => navigate("/admin-sessions")}>Manage Sessions</Button>
          </div>
          <SessionCard />
          <SessionCard />
          <SessionCard />
          <SessionCard />
          <SessionCard />
          <SessionCard />
          <SessionCard />
        </div>
      </div>
    </div>
  );
};

export default SessionsView;
