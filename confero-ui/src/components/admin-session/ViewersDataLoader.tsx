import React from "react"
import { Button } from "@/components/ui/button"
import { X, Upload } from "lucide-react"

const  ViewersDataLoader = () => {
  const [fileName, setFileName] = React.useState("EmailsOfViewers.csv")

  const handleRemoveFile = () => {
    setFileName("")
  }

  const handleLoadViewers = () => {
    console.log("Load viewers")
  }

  return (
    <div className="flex items-center justify-center ">
      <div className="flex items-center flex-col sm:flex-row sm:space-x-4 space-y-4 sm:space-y-0">
        {fileName && (
          <div className="flex items-center bg-white rounded-full px-4 py-2 shadow-md">
            <span className="text-sm font-medium">{fileName}</span>
            <Button
              variant="ghost"
              size="icon"
              className="ml-2 h-5 w-5"
              onClick={handleRemoveFile}
            >
              <X className="h-3 w-3" />
              <span className="sr-only">Remove file</span>
            </Button>
          </div>
        )}
        <Button
          variant="secondary"
          className="flex items-center space-x-2"
          onClick={handleLoadViewers}
        >
          <Upload className="h-4 w-4" />
          <span>Load viewers</span>
        </Button>
      </div>
    </div>
  )
}

export default ViewersDataLoader;