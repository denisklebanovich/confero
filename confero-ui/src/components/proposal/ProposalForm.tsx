import {zodResolver} from "@hookform/resolvers/zod"
import {useForm} from "react-hook-form"
import {z} from "zod"

import {Button} from "@/components/ui/button"
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form"
import {Input} from "@/components/ui/input"
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select"
import OrcidInput from "@/components/orcid/OrcidInput"

const orcidSchema = z.string().regex(/^(\d{4}-){3}\d{3}[\dX]$|^\d{16}$/, {
    message: "Invalid ORCID format. It should be 16 digits with optional hyphens.",
})

const formSchema = z.object({
    title: z.string().min(2).max(50),
    description: z.string().min(2).max(50),
    organisers: z.array(z.object({
        orcid: orcidSchema,
        name: z.string().optional(),
    })).min(1),
    type: z.string().min(2).max(50),
})

const ProposalForm = () => {
    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            title: "",
            type: "SESSION",
            organisers: [],
            description: "",
        },
    })

    function onSubmit(values: z.infer<typeof formSchema>) {
        console.log(values)
    }

    return (
        <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="flex flex-col gap-2 w-1/2">
                <FormField
                    control={form.control}
                    name="title"
                    render={({field}) => (
                        <FormItem>
                            <FormLabel>Title</FormLabel>
                            <FormControl>
                                <Input placeholder="Enter the title" {...field} />
                            </FormControl>
                            <FormMessage/>
                        </FormItem>
                    )}
                />
                <FormField
                    control={form.control}
                    name="type"
                    render={({field}) => (
                        <FormItem className='w-1/4'>
                            <FormLabel>Type</FormLabel>
                            <Select onValueChange={field.onChange} defaultValue={field.value}>
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
                    name="organisers"
                    render={({field}) => (
                        <FormItem>
                            <FormLabel>Organisers</FormLabel>
                            <FormControl>
                                <OrcidInput
                                    value={field.value}
                                    onChange={field.onChange}
                                />
                            </FormControl>
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
                                <Input placeholder="Enter the description" {...field} />
                            </FormControl>
                            <FormMessage/>
                        </FormItem>
                    )}
                />
                <div className='flex flex-row justify-between items-center mt-4'>
                    <Button variant='secondary'>Back</Button>
                    <div className='flex flex-row gap-4'>
                        <Button variant='secondary'>Save as draft</Button>
                        <Button>Submit</Button>
                    </div>
                </div>
            </form>
        </Form>
    )
}

export default ProposalForm