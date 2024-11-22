import {useForm} from "react-hook-form";
import {z} from "zod";

import {Button} from "@/components/ui/button";
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage,} from "@/components/ui/form";
import {Input} from "@/components/ui/input";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue,} from "@/components/ui/select";
import {Textarea} from "@/components/ui/textarea";
import {Badge} from "@/components/ui/badge";
import {useEffect, useState} from "react";
import PresentationForm from "@/components/presentations/PresentationForm";
import {Plus, X} from "lucide-react";
import {Separator} from "@/components/ui/separator";
import {
    ApplicationPreviewResponse,
    ApplicationResponse,
    CreateApplicationRequest,
    PresentationRequest,
    PresenterRequest,
    PresenterResponse,
    SessionType,
    UpdateApplicationRequest
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

const descriptionSchema = z.string()
    .min(1, {message: "Description is required"})
    .max(500, {message: "Description must be at most 500 characters long"});

const formSchema = z.object({
    title: z.string({message: "Title is required"}).min(2).max(100),
    type: z.string({message: "Type is required"}),
    description: descriptionSchema,
    tags: z.array(z.string()).optional(),
    presentations: z
        .array(
            z.object({
                title: z.string().min(2).max(100),
                description: descriptionSchema,
                presenters: z
                    .array(
                        z.object({
                            id: z.number().optional(),
                            isSpeaker: z.boolean(),
                            name: z.string(),
                            orcid: orcidSchema,
                            organization: z.string().nullable().optional(),
                            surname: z.string(),
                            title: z.string().nullable().optional(),
                            email: z.string().optional(),
                        })
                    )
                    .min(1, {message: "At least one presenter is required"}),
            })
        )
        .min(1, {message: "At least one presentation is required"}),
});

type FormValues = z.infer<typeof formSchema>;

interface ProposalFormProps {
    proposalId?: string;
    proposal?: ApplicationResponse;
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

    useEffect(() => {
        if (proposal) {
            form.reset({
                title: proposal.title,
                type: proposal.type,
                description: proposal.description,
                tags: proposal.tags || [],
                presentations: proposal.presentations || [],
            });
        }
    }, [proposal, form]);

    const [newTag, setNewTag] = useState<string>("")
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

    const descriptionValue = watch("description");
    const tagsValue = watch("tags");
    const presentationsValue = watch("presentations");

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
                const uniqueTags = new Set([...tagsValue, ...tags]);
                setValue("tags", Array.from(uniqueTags));
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

    const handleAddTag = () => {
        if (newTag.trim() !== "") {
            setValue("tags", [...tagsValue, newTag.trim()]);
            setNewTag("");
        }
    };

    const handleRemoveTag = (tagToRemove: string) => {
        setValue("tags", tagsValue.filter(tag => tag !== tagToRemove));
    };

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

    const transformPresenters = (presenters: PresenterResponse[]): PresenterRequest[] =>
        presenters.map(({orcid, email, isSpeaker}) => ({
            orcid: orcid || "",
            email: email || "",
            isSpeaker: isSpeaker || false,
        }));

    const transformPresentations = (presentations: FormValues["presentations"]) =>
        presentations.map((presentation) => ({
            ...presentation,
            presenters: transformPresenters(presentation.presenters),
        }));

    const handleDescriptionValidation = (e, fieldOnChange, form) => {
        fieldOnChange(e);
        const value = e.target.value;
        const result = descriptionSchema.safeParse(value);

        if (result.success) {
            form.clearErrors("description");
        } else {
            form.setError("description", {
                type: "manual",
                message: result.error.errors[0].message,
            });
        }
    };

    const showDeleteButton = currentPath.startsWith("/proposal-edit/") && (proposal?.status === "DRAFT" || proposal?.status === "PENDING");
    const showSaveAsDraftButton = currentPath === "/proposal";

    return (
        <Form {...form}>
            <form
                onKeyDown={(e) => {
                    if (e.key === "Enter") {
                        e.preventDefault();
                    }
                }}
                className="flex flex-col gap-2 w-1/2"
                onSubmit={handleSubmit(async (data) => {
                    const transformedData = {
                        ...data,
                        presentations: transformPresentations(data.presentations),
                    };
                    if (currentPath.startsWith("/proposal-edit/")) {
                        handleUpdateProposal(transformedData);
                    } else if (currentPath === "/proposal") {
                        handleCreateProposal(transformedData);
                    } else {
                        toast({
                            title: "Invalid path",
                            description: "The current path is not recognized.",
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
                                    onInput={(e) => handleDescriptionValidation(e, field.onChange, form)}
                                />
                            </FormControl>
                            <FormMessage/>
                        </FormItem>
                    )}
                />
                <FormItem>
                    <FormLabel>Tags</FormLabel>
                    <div className="flex-col w-full items-center justify-between space-x-2 space-y-2">
                        <div className="flex space-x-2">
                            <Input
                                type="text"
                                placeholder="Add a tag"
                                value={newTag}
                                onChange={(e) => setNewTag(e.target.value)}
                                className="flex-grow"
                            />
                            <Button
                                type="button"
                                variant="default"
                                onClick={handleAddTag}
                            >
                                Add Tag
                            </Button>
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
                        </div>
                        <div className="flex flex-wrap gap-2">
                            {tagsValue.map((tag, index) => (
                                <Badge key={index} variant="secondary"
                                       className="flex h-10 flex-row pr-0 justify-between">
                                    {tag}
                                    <Button
                                        type="button"
                                        variant="ghost"
                                        size="icon"
                                        onClick={() => handleRemoveTag(tag)}
                                        className="ml-2"
                                    >
                                        <X/>
                                    </Button>
                                </Badge>
                            ))}
                        </div>
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
                                    control={control}
                                />
                            </div>
                        ))}
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
