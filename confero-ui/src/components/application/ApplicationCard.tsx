import { Calendar } from "lucide-react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { ApplicationStatus, PresenterResponse } from "@/generated";


interface ApplicationCardProps {
    onClick?: () => void;
    title: string;
    organisers: PresenterResponse[];
    date: string;
    status: string;
}

export const STATUS_COLORS = {
    [ApplicationStatus.ACCEPTED]: "bg-green-100 text-green-800 hover:bg-green-200",
    [ApplicationStatus.REJECTED]: "bg-red-100 text-red-800 hover:bg-red-200",
    [ApplicationStatus.DRAFT]: "bg-blue-100 text-blue-800 hover:bg-blue-200",
    [ApplicationStatus.PENDING]: "bg-yellow-100 text-yellow-800 hover:bg-yellow-200",
    [ApplicationStatus.CHANGE_REQUESTED]: "bg-orange-100 text-orange-800 hover:bg-orange-200",
};

export default function ApplicationCard({ title, organisers, onClick, date, status }: ApplicationCardProps) {

    

    return (
        <Card onClick={onClick} className="max-w-md cursor-pointer">
            <CardHeader>
                <CardTitle className="text-xl font-semibold">{title}</CardTitle>
            </CardHeader>
            <CardContent className="grid gap-4">
                <div className="text-sm">
                    <span className="font-semibold">Organizers:</span>
                    {organisers.map(presenter => `${presenter.name} ${presenter.surname}`).join(", ")}
                </div>
                <div className="flex items-center justify-between">
                    <div className="flex items-center text-sm text-muted-foreground">
                        <Calendar className="mr-1 h-4 w-4" />
                        {date}
                    </div>
                    <Badge variant="outline"  className={STATUS_COLORS[status]}>
                        {status}
                    </Badge>
                </div>
            </CardContent>
        </Card>
    )
}