import {
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import OrcidInput from "@/components/orcid/OrcidInput";

const PresentationForm = ({ index, onDelete, presentation, control }) => {

  return (
    <div className="flex flex-col border p-6 border-gray-300 rounded-md gap-4">
      <FormField
        control={control}
        name={`presentations.${index}.title`}
        render={({ field }) => (
          <FormItem>
            <FormLabel>Title</FormLabel>
            <FormControl>
              <Input {...field} />
            </FormControl>
            <FormMessage />
          </FormItem>
        )}
      />
      <FormField
        control={control}
        name={`presentations.${index}.description`}
        render={({ field }) => (
          <FormItem>
            <FormLabel>Description</FormLabel>
            <FormControl>
              <Textarea {...field} />
            </FormControl>
            <FormMessage />
          </FormItem>
        )}
      />

      <FormField
        control={control}
        name={`presentations.${index}.presenters`}
        render={({ field }) => (
          <FormItem>
            <FormLabel>Organisers</FormLabel>
            <FormControl>
              <OrcidInput
                value={field.value || []}
                onChange={field.onChange}
              />
            </FormControl>
            <FormMessage />
          </FormItem>
        )}
      />
      <div className="w-1/4 container mx-auto gap-2">
        <Button type="button" variant="destructive" onClick={onDelete}>
          Delete Presentation
        </Button>
      </div>
    </div>
  );
};

export default PresentationForm;
