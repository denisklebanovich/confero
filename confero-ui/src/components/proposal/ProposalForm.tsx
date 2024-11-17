import {useForm} from "react-hook-form";
import {z} from "zod";

import {Button} from "@/components/ui/button";
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage,} from "@/components/ui/form";
import {Input} from "@/components/ui/input";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue,} from "@/components/ui/select";
import {Textarea} from "@/components/ui/textarea";
import {Badge} from "@/components/ui/badge";
import {useEffect} from "react";
import PresentationForm from "@/components/presentations/PresentationForm";
import {Plus} from "lucide-react";
import {Separator} from "@/components/ui/separator";
import {
    ApplicationPreviewResponse,
    ApplicationResponse,
    CreateApplicationRequest,
    PresentationRequest, PresenterResponse,
    SessionType, UpdateApplicationRequest
} from "@/generated";
import {useApi} from "@/api/useApi.ts";
import {useToast} from "@/hooks/use-toast.ts";
import {zodResolver} from "@hookform/resolvers/zod";
import useTags from "@/hooks/useTags.ts";
import {useLocation, useNavigate} from "react-router-dom";

const orcidSchema = z.string().regex(/^(\d{4}-){3}\d{3}[\dX]$|^\d{16}$/, {
    message:
        "Invalid ORCID format. It should be 16 digits with optional hyphens.",
});

const formSchema = z.object({
    title: z.string({message: "Title is required"}).min(2).max(100),
    type: z.string({message: "Type is required"}),
    description: z.string().min(2).max(500),
    tags: z.array(z.string()).optional(),
    presentations: z
        .array(
            z.object({
                title: z.string().min(2).max(100),
                description: z.string().min(2).max(500),
                presenters: z
                    .array(
                        z.object({
                            orcid: orcidSchema,
                            email: z.string().email(),
                            isSpeaker: z.boolean(),
                        })
                    )
                    .min(1),
            })
        )
        .min(1),
});

type FormValues = z.infer<typeof formSchema>;

interface ProposalFormProps {
    proposalId?: string;
    proposal?: ApplicationResponse;
    isDisabled?: boolean;
}

const ProposalForm = ({proposal, proposalId}: ProposalFormProps) => {
    const form = useForm<FormValues>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            title: proposal?.title,
            type: proposal?.type,
            description: proposal?.description,
            tags: proposal?.tags || [],
            presentations: proposal?.presentations || [],
        },
    });

    const {loading: loadingTags, analyzeText} = useTags();
    const {toast} = useToast();
    const {apiClient, useApiMutation} = useApi();
    const navigate = useNavigate()
    const location = useLocation();
    const currentPath = location.pathname;


    const {mutate: createProposal} = useApiMutation<ApplicationPreviewResponse, CreateApplicationRequest>(
        (request) => apiClient.application.createApplication(request),
        {
            onSuccess: (res) => {
                navigate("/applications")
                if (res.status === "DRAFT") {
                    toast({
                        title: "Draft saved",
                        description: "Your proposal has been saved as a draft.",
                        variant: "success",
                    });
                } else {
                    toast({
                        title: "Proposal submitted",
                        description: "Your proposal has been submitted. Please wait for approval.",
                        variant: "success",
                    });
                }
            },
            onError: (error) => {
                toast({
                    title: "An error occurred",
                    description: error.message,
                    variant: "error",
                });
            },
        }
    );

    const {mutate: updateProposal} = useApiMutation<ApplicationPreviewResponse, UpdateApplicationRequest>(
        (request) => apiClient.application.updateApplication(Number(proposalId), request),
        {
            onSuccess: () => {
                navigate("/applications");
                toast({
                    title: "Proposal edited",
                    description: "Your proposal has been edited.",
                    variant: "success",
                });
            },
            onError: (error) => {
                toast({
                    title: "An error occurred",
                    description: error.message,
                    variant: "error",
                });
            },
        }
    );


    const {mutate: deleteProposal} = useApiMutation<void, { id: string }>(
        (args) => apiClient.application.deleteApplication(Number(args.id)),
        {
            onSuccess: () => {
                navigate("/applications");
                toast({
                    title: "Proposal deleted",
                    description: "Your proposal has been deleted.",
                    variant: "success",
                });
                navigate("/applications");
            },
            onError: (error) => {
                toast({
                    title: "An error occurred",
                    description: error.message,
                    variant: "error",
                });
            },
        }
    );

    const {control, watch, setValue, handleSubmit} = form;
    const isDisabled = false;

    const titleValue = watch("title");
    const typeValue = watch("type");
    const descriptionValue = watch("description");
    const tagsValue = watch("tags");
    const presentationsValue = watch("presentations");

    useEffect(() => {
        console.log(titleValue, typeValue, descriptionValue, tagsValue);
    }, [titleValue, typeValue, descriptionValue, tagsValue, presentationsValue]);

    const handleCreateProposal = (data: FormValues, asDraft?: boolean) => {
        createProposal({
            title: data.title,
            type: data.type as SessionType,
            description: data.description,
            tags: data.tags,
            presentations: data.presentations as PresentationRequest[],
            saveAsDraft: !!asDraft,
        });
    };

    const handleUpdateProposal = (data: FormValues) => {
        updateProposal({
            title: data.title,
            type: data.type as SessionType,
            description: data.description,
            tags: data.tags,
            presentations: data.presentations as PresentationRequest[],
            saveAsDraft: false,
        });
    }

    async function updateTags(e) {
        e.preventDefault();
        console.log("Tags updated");
        if (descriptionValue) {
            try {
                const tags = await analyzeText(descriptionValue);
                setValue("tags", tags);
            } catch (error) {
                console.error("Error analyzing text:", error);
            }
        } else {
            toast({
                title: "Description is required",
                description: "Please enter a description to generate tags.",
                variant: "info",
            })
        }
    }

    const addPresentation = () => {
        setValue("presentations", [
            ...presentationsValue,
            {
                title: "",
                description: "",
            },
        ]);
    };

    const deletePresentation = (index: number) => {
        const updatedPresentations = presentationsValue.filter(
            (_, idx) => idx !== index
        );
        setValue("presentations", updatedPresentations);
    };

    const showDeleteButton = currentPath.startsWith("/proposal-edit/") && (proposal?.status === "DRAFT" || proposal?.status === "PENDING");
    const showSaveAsDraftButton = currentPath === "/proposal";

    return (
        <Form {...form}>
            <form
                className="flex flex-col gap-2 w-1/2"
                onSubmit={handleSubmit(async (data) => {
                    try {
                        formSchema.parse(data);
                        if (currentPath.startsWith("/proposal-edit/")) {
                            handleUpdateProposal(data);

                        } else if (currentPath === "/proposal") {
                            handleCreateProposal(data);
                        } else {
                            toast({
                                title: "Invalid path",
                                description: "The current path is not recognized.",
                                variant: "error",
                            });
                        }
                    } catch (e) {
                        toast({
                            title: "Invalid form",
                            description: "Please fill in all required fields.",
                            variant: "error",
                        });
                    }
                })}
            >
                <FormField
                    control={form.control}
                    name="title"
                    render={({field}) => (
                        <FormItem>
                            <FormLabel>Title</FormLabel>
                            <FormControl>
                                <Input
                                    placeholder="Enter the title"
                                    {...field}
                                    disabled={isDisabled}
                                />
                            </FormControl>
                            <FormMessage/>
                        </FormItem>
                    )}
                />
                <FormField
                    control={form.control}
                    name="type"
                    render={({field}) => (
                        <FormItem className="w-1/4">
                            <FormLabel>Type</FormLabel>
                            <Select
                                onValueChange={field.onChange}
                                defaultValue={field.value}
                                disabled={isDisabled}
                            >
                                <FormControl>
                                    <SelectTrigger>
                                        <SelectValue placeholder="Select type"/>
                                    </SelectTrigger>
                                </FormControl>
                                <SelectContent>
                                    <SelectItem value="SESSION">Session</SelectItem>
                                    <SelectItem value="WORKSHOP">Workshop</SelectItem>
                                    <SelectItem value="TUTORIAL">Tutorial</SelectItem>
                                </SelectContent>
                            </Select>
                            <FormMessage/>
                        </FormItem>
                    )}
                />
                <FormField
                    control={form.control}
                    name="description"
                    render={({field}) => (
                        <FormItem>
                            <FormLabel>Description</FormLabel>
                            <FormControl>
                                <Textarea
                                    placeholder="Enter the description"
                                    {...field}
                                    disabled={isDisabled}
                                />
                            </FormControl>
                            <FormMessage/>
                        </FormItem>
                    )}
                />
                <FormItem>
                    <FormLabel>Tags</FormLabel>
                    <div className="flex w-full items-center justify-between space-x-2">
                        <div className="flex flex-wrap gap-2">
                            {tagsValue.map((tag, index) => (
                                <Badge key={index} variant="secondary" className="text-xs">
                                    {tag}
                                </Badge>
                            ))}
                        </div>
                        {isDisabled ? null : (
                            <Button
                                type="button"
                                variant="outline"
                                onClick={updateTags}
                                disabled={loadingTags}
                            >
                                {loadingTags ? (
                                    <>
                                        <span className="mr-2">
                                            <svg className="animate-spin h-4 w-4 text-primary"
                                                 xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                                <circle className="opacity-25" cx="12" cy="12" r="10"
                                                        stroke="currentColor" strokeWidth="4"></circle>
                                                    <path className="opacity-75" fill="currentColor"
                                                          d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                            </svg>
                                        </span>
                                        Generating tags...
                                    </>
                                ) : (
                                    'Generate tags'
                                )}
                            </Button>
                        )}
                    </div>
                </FormItem>

                <Separator orientation="horizontal" className="w-full my-8"/>

                <FormItem>
                    <FormLabel>Presentations</FormLabel>
                    <div className="flex flex-col gap-4">
                        {presentationsValue.map((presentation, index) => (
                            <div key={index}>
                                <PresentationForm
                                    index={index}
                                    onDelete={() => deletePresentation(index)}
                                    presentation={presentation}
                                    control={control}
                                />
                            </div>
                        ))}
                        {isDisabled ? null : (
                            <div className="w-full">
                                <Button
                                    type="button"
                                    variant="outline"
                                    onClick={addPresentation}
                                >
                                    <Plus className="h-4 w-4"/>
                                    Add Presentation
                                </Button>
                            </div>
                        )}
                    </div>
                </FormItem>
                <div className="flex flex-row justify-between items-center mt-4">
                    <Button
                        variant="secondary"
                        onClick={() => {
                            navigate("/applications")
                        }}
                    >
                        Back
                    </Button>

                    <div className="flex flex-row gap-4">
                        {showDeleteButton && (
                            <Button
                                variant="destructive"
                                onClick={(e) => {
                                    e.preventDefault();
                                    deleteProposal({id: proposalId});
                                }}
                            >
                                Delete Application
                            </Button>
                        )}
                        {showSaveAsDraftButton && (
                            <Button
                                variant="secondary"
                                onClick={async () => {
                                    const formData = form.getValues();
                                    try {
                                        formSchema.parse(formData);
                                        handleCreateProposal(formData, true);
                                    } catch (e) {
                                        toast({
                                            title: "Invalid form",
                                            description: "Please fill in all required fields.",
                                            variant: "error",
                                        });
                                    }
                                }}
                            >
                                Save as draft
                            </Button>
                        )}
                        <Button
                            variant="default"
                            type="submit"
                        >
                            Submit
                        </Button>
                    </div>
                </div>
            </form>
        </Form>
    );
};

export default ProposalForm;
