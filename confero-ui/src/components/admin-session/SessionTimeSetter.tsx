import { Button } from "@/components/ui/button";
import {
  Card,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { useNavigate } from "react-router-dom";

const SessionTimeSetter = () => {
  const navigate = useNavigate();

  return (
    <Card className="w-full">
      <CardHeader>
        <div className={"w-full flex justify-between"}>
          <CardTitle className="text-xl">
            Computer Vision and Intelligent systems
          </CardTitle>

          <div className="flex flex-col sm:flex-row items-center space-y-2 sm:space-y-0 sm:space-x-2 pb-2">
            <Button
              className="w-full sm:w-auto mr-2"
              onClick={() => navigate("/timetable")}
            >
              Set timetable
            </Button>
          </div>
        </div>
        <p className="text-sm text-muted-foreground">
          Organizers: Van-Dung Hoang, Dinh-Hien Nguyen, Huyen Trang Phan and
          Kang-Hyun Jo
        </p>
      </CardHeader>
    </Card>
  );
};

export default SessionTimeSetter;
