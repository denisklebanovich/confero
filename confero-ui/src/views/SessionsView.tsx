import SessionCard from "@/components/sessions/SessionCard.tsx";
import { useApi } from "@/api/useApi.ts";
import { useQueryClient } from "@tanstack/react-query";
import {
  CreateApplicationRequest,
  SessionPreviewResponse,
  SessionType,
} from "@/generated";
import { Button } from "@/components/ui/button";
import { useNavigate } from "react-router-dom";

const SessionsView = () => {
  const { apiClient, useApiQuery, useApiMutation } = useApi();

  const queryClient = useQueryClient();

  const navigate = useNavigate();

  const { data: sessions, isLoading } = useApiQuery<SessionPreviewResponse[]>(
    ["sessions"],
    () => apiClient.session.getSessions()
  );

  const createSessionMutation = useApiMutation<
    SessionPreviewResponse,
    CreateApplicationRequest
  >((request) => apiClient.session.createSession(request), {
    onSuccess: (newSession) => {
      queryClient.setQueryData<SessionPreviewResponse[]>(
        ["sessions"],
        (oldSessions) => [...(oldSessions || []), newSession]
      );
    },
  });

  const handleCreateSession = () => {
    createSessionMutation.mutate({
      title: "New Session",
      type: SessionType.SESSION,
      tags: [],
      description: "New Session",
      presenters: [],
      saveAsDraft: false,
    });
  };

  if (isLoading) {
    return <div>Loading...</div>;
  }

  return (
    <div className={"min-w-full min-h-full"}>
      <Button className="mb-10" onClick={handleCreateSession}>
        Create test session
      </Button>
      <div className="flex w-full justify-center">
        <div className={"w-2/3 items-center gap-5 flex flex-col"}>
          <div className="flex w-full">
            <div className="text-3xl font-bold w-full">Sessions:</div>
            <Button onClick={() => navigate("/admin-sessions")}>
              Manage Sessions
            </Button>
          </div>
          {sessions?.map((session) => (
            <SessionCard key={session.id} {...session} />
          ))}
        </div>
      </div>
    </div>
  );
};

export default SessionsView;
