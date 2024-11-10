import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button.tsx";
import SessionDescription from "@/components/session/SessionDescription.tsx";
import Room from "@/components/session/Room.tsx";
import SessionFiles from "@/components/session/SessionFiles";
import {useState} from "react";

export default function SessionView() {
  const session = {
      "sessionID": "vladz2323gf", title: "Computer Science Club",
      "description": "Computer vision combined with artificial intelligence has been created to better serve the increasing needs of the people. These kinds of computer vision have been applied in a wide range of areas such as surveillance systems, medical diagnosis, intelligent transportation system, and further on cyber-physical interaction systems. As a technological discipline, computer vision seeks to apply the theories and models of computer vision to the construction of intelligent systems.",
      "organisers": [
        {
            "id": 1,
            "name": "Dinh-Hien",
            "surname": "Nguyen",
            "orcid": "0000-0002-0759-9656",
            "isSpeaker": true
        },
        {
            "id": 2,
            "name": "Huyen",
            "surname": "Nguyen",
            "orcid": "0000-0002-0759-9656",
            "isSpeaker": true,
        },
        {
            "id": 3,
            "name": "Huyen",
            "surname": "Trang Phan",
            "orcid": "0000-0002-0759-9656",
            "isSpeaker": false
        },
        {
            "id": 4,
            "name": "Kang-Hyun",
            "surname": " Jo",
            "orcid": "0000-0002-0759-9656",
            "isSpeaker": false
        }
    ]
  }

  const [isLoggedIn, setIsLoggedIn] = useState(true);


  return (
    <>

      <div className={`w-full flex items-center ${isLoggedIn ? "justify-around" : "justify-center"} mt-2`}>
          {
              isLoggedIn &&
        <Room roomID={session.sessionID} title={session.title}/>
          }

          <SessionDescription title={session.title} description={session.description} organisers={session.organisers}/>
      </div>
        {
            isLoggedIn &&
            <div className={"w-full flex items-center justify-around mt-16"}>
                <SessionFiles/>
            </div>
        }


        <div className={`w-full flex justify-center ${isLoggedIn ? "mt-2" : "mt-20"}`}>
            <Button className={"bg-gray-700 hover:bg-gray-600"}>
          <Link to="/">Back</Link>
        </Button>
      </div>
    </>
  );
}
