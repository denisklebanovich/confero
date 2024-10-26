import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card.tsx";

const SessionDescription = () => {
    return (
        <Card className="w-1/3">
            <CardHeader>
                <CardTitle>Computer Vision and Intelligent systems</CardTitle>
                <CardDescription>
                    Computer vision combined with artificial intelligence has been created to better serve the increasing needs of the people. These kinds of computer vision have been applied in a wide range of areas such as surveillance systems, medical diagnosis, intelligent transportation system, and further on cyber-physical interaction systems. As a technological discipline, computer vision seeks to apply the theories and models of computer vision to the construction of intelligent systems.
                </CardDescription>
            </CardHeader>
            <CardContent>
                <h3 className="font-semibold mb-2">Organizers</h3>
                <ul className="space-y-1 text-sm text-muted-foreground">
                    <li>Van-Dung Hoang, Dinh-Hien</li>
                    <li>Nguyen, Huyen Trang Phan and</li>
                    <li>Kang-Hyun Jo</li>
                </ul>
            </CardContent>
        </Card>
    );
};

export default SessionDescription;