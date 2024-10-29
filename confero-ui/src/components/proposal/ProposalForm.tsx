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
};

const ProposalForm = ({ defaultValues }: ProposalFormProps) => {
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

  function onSubmit(values: z.infer<typeof formSchema>) {
    console.log(values);
  }

  return (
    <Form {...form}>
      <form
        onSubmit={form.handleSubmit(onSubmit)}
        className="flex flex-col gap-2 w-1/2"
      >
        <FormField
          control={form.control}
          name="title"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Title</FormLabel>
              <FormControl>
                <Input placeholder="Enter the title" {...field} />
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
              <Select onValueChange={field.onChange} defaultValue={field.value}>
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
                <OrcidInput value={field.value} onChange={field.onChange} />
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
                <Textarea placeholder="Enter the description" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormItem>
          <FormLabel>Tags</FormLabel>
          <div className="flex w-full items-center justify-between space-x-2">
            <div className="flex flex-wrap gap-2">
              {form.getValues("tags")?.map((tag, index) => (
                <Badge key={index} variant="secondary" className="text-xs">
                  {tag}
                </Badge>
              ))}
            </div>
            <Button variant="secondary">Generate tags</Button>
          </div>
        </FormItem>
        <div className="flex flex-row justify-between items-center mt-4">
          <Button variant="secondary" onClick={() => navigate("/applications")}>Back</Button>
          <div className="flex flex-row gap-4">
            <Button variant="secondary">Save as draft</Button>
            <Button>Submit</Button>
          </div>
        </div>
      </form>
    </Form>
  );
};

export default ProposalForm;
