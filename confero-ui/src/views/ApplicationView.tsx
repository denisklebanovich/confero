import CustomPagination from "@/components/application/CustomPagination.tsx";
import ApplicationCard from "@/components/application/ApplicationCard.tsx";
import {Link, useNavigate} from "react-router-dom";
import {Button} from "@/components/ui/button.tsx";
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select.tsx";
import {useForm} from "react-hook-form";
import {useEffect, useState} from "react";

const ApplicationView = () => {
    const navigate = useNavigate();
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(10);

    const applications = [
        {"title":"Computer Vision and Intelligent systems",
        "organisers": ["Van-Dung Hoang","Van-Dung Hoang","Van-Dung Hoang","Van-Dung Hoang","Van-Dung Hoang"],
        "date": new Date(),
        "status" : "Accepted"
        },
        {"title":"Computer Vision and Intelligent systems",
            "organisers": ["Van-Dung Hoang","Van-Dung Hoang","Van-Dung Hoang","Van-Dung Hoang","Van-Dung Hoang"],
            "date": new Date(),
            "status" : "Draft"
        },
        {"title":"Computer Vision and Intelligent systems",
            "organisers": ["Van-Dung Hoang","Van-Dung Hoang","Van-Dung Hoang","Van-Dung Hoang","Van-Dung Hoang"],
            "date": new Date(),
            "status" : "Pending"
        },
        {"title":"Computer Vision and Intelligent systems",
            "organisers": ["Van-Dung Hoang","Van-Dung Hoang","Van-Dung Hoang","Van-Dung Hoang","Van-Dung Hoang"],
            "date": new Date(),
            "status" : "Rejected"
        }
    ]

    function onPageChange(page){
        setCurrentPage(page);
    }

    const form = useForm({
        defaultValues: {
            year: 'All',
            status: 'All',
            order: 'Descending',
        },
    })

    const { control, watch, setValue } = form;

    const yearValue = watch("year");
    const statusValue = watch("status");
    const orderValue = watch("order");

    useEffect(() => {
        console.log(yearValue, statusValue, orderValue)
    }, [yearValue, statusValue, orderValue]);


    return (
        <div>
            <div className={"w-full flex justify-around"}>
                <div className={"w-1/4"}></div>
                <div className={"w-2/3 justify-around flex flex-row"}>
                    <div className='text-3xl font-bold w-full'>Applications:</div>
                    <Button>
                        <Link to='/proposal'>Add application</Link>
                    </Button>
                </div>
                <div className={"w-1/4"}></div>
            </div>

            <div className={"w-full flex justify-around"}>
                <div className={"w-1/4"}>
                    <div className={"w-full h-full pl-5"}>
                        <Form {...form}  >
                            <form className="space-y-4">
                                <FormField
                                    control={form.control}

                                    name="year"
                                    render={({ field }) => (
                                        <FormItem className="w-40">
                                            <FormLabel>Year</FormLabel>
                                            <Select onValueChange={field.onChange} defaultValue={field.value}>
                                                <FormControl>
                                                    <SelectTrigger>
                                                        <SelectValue placeholder="Select year" />
                                                    </SelectTrigger>
                                                </FormControl>
                                                <SelectContent>
                                                    <SelectItem value="All">All</SelectItem>
                                                    <SelectItem value="2024">2024</SelectItem>
                                                    <SelectItem value="2023">2023</SelectItem>
                                                </SelectContent>
                                            </Select>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />

                                <FormField
                                    control={form.control}
                                    name="status"
                                    render={({ field }) => (
                                        <FormItem className="w-40">
                                            <FormLabel>Status</FormLabel>
                                            <Select onValueChange={field.onChange} defaultValue={field.value}>
                                                <FormControl>
                                                    <SelectTrigger>
                                                        <SelectValue placeholder="Select status" />
                                                    </SelectTrigger>
                                                </FormControl>
                                                <SelectContent>
                                                    <SelectItem value="All">All</SelectItem>
                                                    <SelectItem value="Accepted">Accepted</SelectItem>
                                                    <SelectItem value="Rejected">Rejected</SelectItem>
                                                    <SelectItem value="Pending">Pending</SelectItem>
                                                    <SelectItem value="Draft">Draft</SelectItem>
                                                </SelectContent>
                                            </Select>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />

                                <FormField
                                    control={form.control}
                                    name="order"
                                    render={({ field }) => (
                                        <FormItem className="w-40">
                                            <FormLabel>Order</FormLabel>
                                            <Select onValueChange={field.onChange} defaultValue={field.value}>
                                                <FormControl>
                                                    <SelectTrigger>
                                                        <SelectValue placeholder="Select order" />
                                                    </SelectTrigger>
                                                </FormControl>
                                                <SelectContent>
                                                    <SelectItem value="Descending">Descending</SelectItem>
                                                    <SelectItem value="Ascending">Ascending</SelectItem>
                                                </SelectContent>
                                            </Select>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                            </form>
                        </Form>
                    </div>
                </div>
                <div className={"grid grid-cols-2 mt-5 gap-y-4 gap-x-4 w-2/3 overflow-auto max-h-[55vh] px-2"}>
                    {applications.map((application)=>
                        <ApplicationCard  title={application.title} date={application.date} status={application.status} organisers={application.organisers} onClick={() => navigate("/proposal-edit")}/>
                    )}

                </div>
                <div className={"w-1/4"}>

                </div>
            </div>
            <CustomPagination currentPage={currentPage} totalPages={totalPages} onPageChange={onPageChange}/>
        </div>
    );
};

export default ApplicationView;