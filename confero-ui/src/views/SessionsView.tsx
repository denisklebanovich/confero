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
import {useState} from "react";
import Event from "@/components/sessions/Event.tsx";

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

  const mockEvents =[
    {
      "id": "1",
      "type": "FILE_UPLOAD",
      "userFirstName": "John",
      "userLastName": "Doe",
      "timestamp": "2024-11-11T12:49:41.805Z",
      "sessionId": "abc123"
    },
    {
      "id": "2",
      "type": "FILE_UPLOAD",
      "userFirstName": "Jane",
      "userLastName": "Smith",
      "timestamp": "2024-11-11T12:50:25.123Z",
      "sessionId": "def456"
    },
    {
      "id": "3",
      "type": "FILE_UPLOAD",
      "userFirstName": "Alice",
      "userLastName": "Johnson",
      "timestamp": "2024-11-11T12:52:11.567Z",
      "sessionId": "ghi789"
    },
    {
      "id": "4",
      "type": "FILE_UPLOAD",
      "userFirstName": "Bob",
      "userLastName": "Brown",
      "timestamp": "2024-11-11T12:54:02.901Z",
      "sessionId": "jkl012"
    },
    {
      "id": "5",
      "type": "FILE_UPLOAD",
      "userFirstName": "Charlie",
      "userLastName": "Davis",
      "timestamp": "2024-11-11T12:56:45.678Z",
      "sessionId": "mno345"
    }
  ]


  const [events, setEvents] = useState(mockEvents);



  const handleCreateSession = () => {
    createSessionMutation.mutate({
      title: "New Session",
      type: SessionType.SESSION,
      tags: [],
      description: "New Session",
      presentations: [],
      saveAsDraft: false,
    });
  };

  if (isLoading) {
    return <div>Loading...</div>;
  }

  return (
    <div className={"min-w-full min-h-full"}>

      <div className="flex w-full justify-around">
        <div className={"w-1/5"}></div>
        <div className={"w-2/3 items-center gap-5 flex flex-col"}>
          <div className="flex w-full">
            <div className="text-3xl font-bold w-full">All Sessions:</div>
            <Button className="mr-2" onClick={handleCreateSession}>
              Create test session
            </Button>
            <Button onClick={() => navigate("/my-calendar")}>
              View my calendar
            </Button>
          </div>
          {sessions?.map((session) => (
              <SessionCard key={session.id} {...session} />
          ))}
        </div>
        <div className={"w-1/5 flex flex-col justify-center pl-8 ml-5 mt-12"}>
          {events.map(event => <Event sessionId={event.sessionId} userFirstName={event.userFirstName} userLastName={event.userLastName}/>)}
        </div>
      </div>
    </div>
  );
};

export default SessionsView;
