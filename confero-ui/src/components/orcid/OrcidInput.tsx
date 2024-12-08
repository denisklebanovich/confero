import {useState} from 'react'
import {Input} from "@/components/ui/input"
import {Button} from "@/components/ui/button"
import {Badge} from "@/components/ui/badge"
import {X, Star} from 'lucide-react'
import {OrcidInfoResponse, PresenterResponse} from "@/generated"
import {useApi} from "@/api/useApi.ts"
import {useToast} from "@/hooks/use-toast.ts";

interface OrcidInputProps {
    value: PresenterResponse[];
    onChange: (value: PresenterResponse[]) => void;
}

export default function OrcidInput({value, onChange}: OrcidInputProps) {
    const [currentORCID, setCurrentORCID] = useState('')
    const [currentEmail, setCurrentEmail] = useState('')
    const [isLoading, setIsLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const {apiClient} = useApi()
    const {toast} = useToast()

    const validateORCID = async (orcid: string): Promise<{ valid: boolean; value?: OrcidInfoResponse }> => {
        try {
            const presenter = await apiClient.orcid.getOrcidData(orcid);
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
        setCurrentORCID(e.target.value);
        setError(null);
    };

    const handleEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setCurrentEmail(e.target.value);
        setError(null);
    };

    const handleORCIDSubmit = async () => {
        if (!currentORCID || !currentEmail) {
            setError("Please enter both ORCID and email.");
            return;
        }

        setIsLoading(true);
        setError(null);

        try {
            if (value.find((entry) => entry.orcid === currentORCID)) {
                setError("ORCID already added.");
                return;
            }
            const result = await validateORCID(currentORCID);
            if (result.valid && result.value) {
                const newPresenter: PresenterResponse = {
                    ...result.value,
                    surname: result.value.surname ?? "",
                    isSpeaker: value.length === 0,
                };
                onChange([...value, newPresenter]);
                setCurrentORCID("");
                setCurrentEmail("");
                toast({
                    title: "Presenter added",
                    description: `${result.value.name} has been added as a presenter.`,
                });
            } else {
                setError("Invalid ORCID. Please check and try again.");
            }
        } catch (err) {
            setError("An error occurred while validating the ORCID.");
        } finally {
            setIsLoading(false);
        }
    };

    const removeORCID = (orcid: string) => {
        const updatedPresenters = value.filter((entry) => entry.orcid !== orcid);
        if (updatedPresenters.length > 0 && !updatedPresenters.some(p => p.isSpeaker)) {
            updatedPresenters[0].isSpeaker = true;
        }
        onChange(updatedPresenters);
        toast({
            title: "Presenter removed",
            description: "The presenter has been removed from the list.",
        });
    };

    const toggleMainSpeaker = (e: React.MouseEvent, orcid: string) => {
        e.preventDefault()
        const updatedPresenters = value.map(presenter => ({
            ...presenter,
            isSpeaker: presenter.orcid === orcid
        }));
        onChange(updatedPresenters);
    };

    return (
        <div className="space-y-4">
            <div className="space-y-2">
                <div className="flex space-x-2">
                    <Input
                        type="text"
                        placeholder="Enter ORCID (e.g., 0000-0000-0000-0000)"
                        value={currentORCID}
                        onChange={handleORCIDChange}
                        className="flex-grow"
                    />
                    <Input
                        type="email"
                        placeholder="Enter email"
                        value={currentEmail}
                        onChange={handleEmailChange}
                        className="flex-grow"
                    />
                    <Button onClick={handleORCIDSubmit} disabled={isLoading}>
                        {isLoading ? "Validating..." : "Add"}
                    </Button>
                </div>
                {error && <p className="text-red-500 text-sm">{error}</p>}
            </div>

            <div className="grid grid-cols-1 gap-2">
                {value.map((entry) => (
                    <Badge
                        key={entry.orcid}
                        variant={entry.isSpeaker ? "default" : "secondary"}
                        className="flex flex-row items-center justify-between px-4"
                    >
                        <div className="flex flex-col items-start overflow-hidden">
                            <span className="font-semibold truncate">{entry.name} {entry.surname}</span>
                            <span className="text-xs truncate">ORCID: {entry.orcid}</span>
                        </div>
                        <div className="flex items-center space-x-1">
                            {entry.isSpeaker && (
                                <span className="text-xs truncate">Main Speaker</span>
                            )}
                            <Button
                                variant="ghost"
                                size="sm"
                                onClick={(e) => toggleMainSpeaker(e, entry.orcid)}
                                className="shrink-0"
                                disabled={entry.isSpeaker}
                            >
                                <Star className={`h-4 w-4 ${entry.isSpeaker ? 'fill-current' : ''}`}/>
                                <span className="sr-only">Set as Main Speaker</span>
                            </Button>
                            <Button
                                variant="ghost"
                                size="sm"
                                onClick={() => removeORCID(entry.orcid)}
                                className="shrink-0"
                            >
                                <X className="h-4 w-4"/>
                                <span className="sr-only">Remove</span>
                            </Button>
                        </div>
                    </Badge>
                ))}
            </div>
        </div>
    );
}