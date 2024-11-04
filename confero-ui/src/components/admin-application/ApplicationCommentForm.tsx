import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Textarea } from "@/components/ui/textarea"

const ApplicationCommentForm = ({value, onChange}) => {
  return (
    <div className="flex flex-col items-center justify-center p-4">
      <Card className="w-full">
        <CardHeader>
          <CardTitle className="text-2xl font-semibold">Comment</CardTitle>
          <p className="text-gray-500">Provide information on what to be changed in the application</p>
        </CardHeader>
        <CardContent>
          <Textarea 
            placeholder="Change hte objective. The type of the event is wrong."
            className="min-h-48"
            value={value}
            onChange={onChange}
          />
        </CardContent>
      </Card>
    </div>
  )
}

export default ApplicationCommentForm;