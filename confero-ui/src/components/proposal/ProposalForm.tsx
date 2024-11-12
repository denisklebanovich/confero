import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";

import { Button } from "@/components/ui/button";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Textarea } from "@/components/ui/textarea";
import { Badge } from "@/components/ui/badge";
import { useNavigate } from "react-router-dom";
import { useEffect } from "react";
import PresentationForm from "@/components/presentations/PresentationForm";
import { Plus } from "lucide-react";
import { Separator } from "@/components/ui/separator";

const orcidSchema = z.string().regex(/^(\d{4}-){3}\d{3}[\dX]$|^\d{16}$/, {
  message:
    "Invalid ORCID format. It should be 16 digits with optional hyphens.",
});

const formSchema = z.object({
  title: z.string().min(2).max(50),
  type: z.string().min(2).max(50),
  description: z.string().min(2).max(50),
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
  defaultValues?: Partial<FormValues>;
  isDisabled?: boolean;
}

const ProposalForm = ({
  defaultValues,
  isDisabled = false,
}: ProposalFormProps) => {
  const form = useForm<FormValues>({
    // resolver: zodResolver(formSchema),
    defaultValues: {
      title: "",
      type: "SESSION",
      description: "",
      tags: ["Ai", "Machine Learning", "Deep Learning"],
      presentations: [],
      ...defaultValues,
    },
  });

  const navigate = useNavigate();
  const { control, watch, setValue, handleSubmit } = form;

  const titleValue = watch("title");
  const typeValue = watch("type");
  const descriptionValue = watch("description");
  const tagsValue = watch("tags");
  const presentationsValue = watch("presentations");

  useEffect(() => {
    console.log(titleValue, typeValue, descriptionValue, tagsValue);
  }, [titleValue, typeValue, descriptionValue, tagsValue, presentationsValue]);

  function sendOnDraft(e) {
    e.preventDefault();
    console.log("Draft saved");
  }

  const onSubmit = (data: FormValues) => {
    console.log("Form submitted with values:", data);
  };

  function onApprove(e) {
    e.preventDefault();
    console.log("Approved");
  }

  function onReject(e) {
    e.preventDefault();
    console.log("Rejected");
  }

  function updateTags(e) {
    e.preventDefault();
    console.log("Tags updated");
    setValue("tags", [
      "Computer Vision",
      "Artificial Intelligence",
      "Machine Learning",
    ]);
  }

  const addPresentation = () => {
    setValue("presentations", [
      ...presentationsValue,
      {
        title: "",
        description: "",
        presenters: [{ orcid: "", email: "", isSpeaker: true }],
      },
    ]);
  };

  const deletePresentation = (index: number) => {
    const updatedPresentations = presentationsValue.filter(
      (_, idx) => idx !== index
    );
    setValue("presentations", updatedPresentations);
  };

  return (
    <Form {...form}>
      <form
        onSubmit={handleSubmit(onSubmit)}
        className="flex flex-col gap-2 w-1/2"
      >
        <FormField
          control={form.control}
          name="title"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Title</FormLabel>
              <FormControl>
                <Input
                  placeholder="Enter the title"
                  {...field}
                  disabled={isDisabled}
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="type"
          render={({ field }) => (
            <FormItem className="w-1/4">
              <FormLabel>Type</FormLabel>
              <Select
                onValueChange={field.onChange}
                defaultValue={field.value}
                disabled={isDisabled}
              >
                <FormControl>
                  <SelectTrigger>
                    <SelectValue placeholder="Select type" />
                  </SelectTrigger>
                </FormControl>
                <SelectContent>
                  <SelectItem value="SESSION">Session</SelectItem>
                  <SelectItem value="WORKSHOP">Workshop</SelectItem>
                  <SelectItem value="TUTORIAL">Tutorial</SelectItem>
                </SelectContent>
              </Select>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="description"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Description</FormLabel>
              <FormControl>
                <Textarea
                  placeholder="Enter the description"
                  {...field}
                  disabled={isDisabled}
                />
              </FormControl>
              <FormMessage />
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
              <Button onClick={(e) => updateTags(e)} variant="secondary">
                Generate tags
              </Button>
            )}
          </div>
        </FormItem>

        <Separator orientation="horizontal" className="w-full my-8" />

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
                  <Plus className="h-4 w-4" />
                  Add Presentation
                </Button>
              </div>
            )}
          </div>
        </FormItem>
        <div className="flex flex-row justify-between items-center mt-4">
          <Button variant="secondary" onClick={() => navigate("/applications")}>
            Back
          </Button>
          {isDisabled ? (
            <div className="flex flex-row gap-4">
              <Button variant="secondary" onClick={() => navigate("/comment")}>
                Add a comment
              </Button>
              <Button variant="destructive" onClick={(e) => onReject(e)}>
                Reject
              </Button>
              <Button onClick={(e) => onApprove(e)}>Approve</Button>
            </div>
          ) : (
            <div className="flex flex-row gap-4">
              <Button variant="secondary" onClick={(e) => sendOnDraft(e)}>
                Save as draft
              </Button>
              <Button type="submit">Submit</Button>
            </div>
          )}
        </div>
      </form>
    </Form>
  );
};

export default ProposalForm;
