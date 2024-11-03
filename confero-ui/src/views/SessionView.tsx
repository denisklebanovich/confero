import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button.tsx";
import SessionDescription from "@/components/session/SessionDescription.tsx";
import Room from "@/components/session/Room.tsx";
import SessionFiles from "@/components/session/SessionFiles";

export default function SessionView() {
  const session = {
      "roomID": "vladz2323gf", title: "Computer Science Club",
      "description": "Computer vision combined with artificial intelligence has been created to better serve the increasing needs of the people. These kinds of computer vision have been applied in a wide range of areas such as surveillance systems, medical diagnosis, intelligent transportation system, and further on cyber-physical interaction systems. As a technological discipline, computer vision seeks to apply the theories and models of computer vision to the construction of intelligent systems.",
      "organisers": ["Van-Dung Hoang", "Dinh-Hien","Nguyen", "Huyen Trang Phan"]
  }


  return (
    <>
      <div className={"w-full flex items-center justify-around mt-2"}>
        <Room roomID={session.roomID} title={session.title}/>
        <SessionDescription title={session.title} description={session.description} organisers={session.organisers}/>
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
