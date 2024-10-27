import {useState} from 'react'
import {Input} from "@/components/ui/input"
import {Button} from "@/components/ui/button"
import {Badge} from "@/components/ui/badge"
import {X} from "lucide-react"
import {OrcidInfoResponse} from "@/generated";
import {useApiClient} from "@/api/useApiClient.ts";

interface OrcidInputProps {
    value: OrcidInfoResponse[]
    onChange: (value: OrcidInfoResponse[]) => void
}

export default function OrcidInput({value, onChange}: OrcidInputProps) {
    const [currentORCID, setCurrentORCID] = useState('')
    const [isLoading, setIsLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const apiInstance = useApiClient()

    const validateORCID = async (orcid: string): Promise<{ valid: boolean; value?: OrcidInfoResponse }> => {
        try {
            const presenter = await apiInstance.orcid.getOrcidData(orcid);
            const isValid = /^(\d{4}-){3}\d{3}[\dX]$|^\d{16}$/.test(orcid)
            if (isValid) {
                return {valid: true, value: presenter}
            }
            return {valid: false}
        } catch (e) {
            return {valid: false}
        }
    }

    const handleORCIDChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setCurrentORCID(e.target.value)
        setError(null)
    }

    const handleORCIDSubmit = async () => {
        if (!currentORCID) return

        setIsLoading(true)
        setError(null)

        try {
            if (value.find(entry => entry.orcid === currentORCID)) {
                setError('ORCID already added.')
                return
            }
            const result = await validateORCID(currentORCID)
            if (result.valid) {
                onChange([...value, result.value!!])
                setCurrentORCID('')
            } else {
                setError('Invalid ORCID. Please check and try again.')
            }
        } catch (err) {
            setError('An error occurred while validating the ORCID.')
        } finally {
            setIsLoading(false)
        }
    }

    const removeORCID = (orcid: string) => {
        onChange(value.filter(entry => entry.orcid !== orcid))
    }

    return (
        <div className="space-y-4">
            <div className="flex space-x-2">
                <Input
                    type="text"
                    placeholder="Enter ORCID (e.g., 0000-0000-0000-0000)"
                    value={currentORCID}
                    onChange={handleORCIDChange}
                    className="flex-grow"
                />
                <Button onClick={handleORCIDSubmit} disabled={isLoading}>
                    {isLoading ? 'Validating...' : 'Add'}
                </Button>
            </div>

            {error && <p className="text-red-500 text-sm">{error}</p>}

            <div className="grid grid-cols-2 gap-2">
                {value.map(entry => (
                    <Badge key={entry.orcid} variant="secondary"
                           className="flex items-center justify-between p-2  pl-4 w-full">
                        <span>{entry.name} {entry.surname}</span>
                        <Button variant="ghost" size="sm" onClick={() => removeORCID(entry.orcid)}>
                            <X className="h-4 w-4"/>
                            <span className="sr-only">Remove</span>
                        </Button>
                    </Badge>
                ))}
            </div>
        </div>
    )
}