import SessionTimeSetter from "@/components/admin-session/SessionTimeSetter.tsx";
import ViewersDataLoader from "@/components/admin-session/ViewersDataLoader";
import { Label } from "@/components/ui/label";
import { Switch } from "@/components/ui/switch";

const AdminSessionView = () => {
  return (
    <div className={"min-w-full min-h-full"}>
      <div className="flex w-full justify-center">
        <div className={"w-3/4 items-center gap-5 flex flex-col"}>
          <div className="text-3xl font-bold w-full flex justify-between">
            Sessions:
            <div className="flex items-center gap-3">
              <Switch />
              <Label>Accepting Applications</Label>
            </div>
          </div>
          <SessionTimeSetter />
          <SessionTimeSetter />
          <SessionTimeSetter />
          <SessionTimeSetter />
          <div className="flex w-full justify-end">
            <ViewersDataLoader />
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminSessionView;
