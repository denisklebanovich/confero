import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button.tsx";
import SessionDescription from "@/components/session/SessionDescription.tsx";
import Room from "@/components/session/Room.tsx";
import SessionFiles from "@/components/session/SessionFiles";

export default function SessionView() {
  return (
    <>
      <div className={"w-full flex items-center justify-around mt-2"}>
        <Room />
        <SessionDescription />
      </div>
      <div className={"w-full flex items-center justify-around mt-16"}>
        <SessionFiles />
      </div>

      <div className={"w-full left-0 flex justify-center"}>
        <Button className={"bg-gray-700 hover:bg-gray-600"}>
          <Link to="/">Back</Link>
        </Button>
      </div>
    </>
  );
}
