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
import OrcidInput from "@/components/orcid/OrcidInput";
import { Textarea } from "@/components/ui/textarea";
import { Badge } from "@/components/ui/badge";
import { useNavigate } from "react-router-dom";
import {useEffect} from "react";

const orcidSchema = z.string().regex(/^(\d{4}-){3}\d{3}[\dX]$|^\d{16}$/, {
  message:
    "Invalid ORCID format. It should be 16 digits with optional hyphens.",
});

const formSchema = z.object({
  title: z.string().min(2).max(50),
  description: z.string().min(2).max(50),
  organisers: z
    .array(
      z.object({
        orcid: orcidSchema,
        name: z.string().optional(),
      })
    )
    .min(1),
  type: z.string().min(2).max(50),
  tags: z.array(z.string()).optional(),
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
    resolver: zodResolver(formSchema),
    defaultValues: {
      title: "",
      type: "SESSION",
      organisers: [],
      description: "",
      tags: ["Ai", "Machine Learning", "Deep Learning"],
      ...defaultValues,
    },
  });

  const navigate = useNavigate();
    const { control, watch, setValue } = form;

    const titleValue = watch("title");
    const typeValue = watch("type");
    const organisersValue = watch("organisers");
    const descriptionValue = watch("description");
    const tagsValue = watch("tags");

    useEffect(() => {
        console.log(titleValue, typeValue, organisersValue, descriptionValue, tagsValue)
    }, [titleValue, typeValue, organisersValue, descriptionValue, tagsValue]);

    function sendOnDraft(e) {
        e.preventDefault();
        console.log("Draft saved")
    }

    function onSubmit(e) {
        e.preventDefault();
        console.log("Submit")
    }

    function onApprove(e) {
        e.preventDefault();
        console.log("Approved")
    }

    function onReject(e) {
        e.preventDefault();
        console.log("Rejected")
    }

    function updateTags(e){
        e.preventDefault();
        console.log("Tags updated")
        setValue("tags", ["Computer Vision", "Artificial Intelligence", "Machine Learning"])
    }


    return (
    <Form {...form}>
      <form
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
          name="organisers"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Organisers</FormLabel>
              <FormControl>
                <OrcidInput
                  value={field.value}
                  onChange={field.onChange}
                  isDisabled={isDisabled}
                />
              </FormControl>
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
              <Button onClick={(e) => updateTags(e)} variant="secondary">Generate tags</Button>
            )}
          </div>
        </FormItem>
        <div className="flex flex-row justify-between items-center mt-4">
          <Button variant="secondary" onClick={() => navigate("/applications")}>
            Back
          </Button>
          {isDisabled ? (
            <div className="flex flex-row gap-4">
              <Button variant="secondary" onClick={() => navigate("/comment")}>Add a comment</Button>
              <Button variant="destructive" onClick={(e)=>onReject(e)} >Reject</Button>
              <Button onClick={(e)=>onApprove(e)} >Approve</Button>
            </div>
          ) : (
            <div className="flex flex-row gap-4">
              <Button variant="secondary" onClick={(e)=>sendOnDraft(e)}>Save as draft</Button>
              <Button onClick={(e)=>onSubmit(e)}>Submit</Button>
            </div>
          )}
        </div>
      </form>
    </Form>
  );
};

export default ProposalForm;
