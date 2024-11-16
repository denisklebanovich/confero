import CustomPagination from "@/components/application/CustomPagination.tsx";
import ApplicationCard from "@/components/application/ApplicationCard.tsx";
import {Link, useNavigate} from "react-router-dom";
import {Button} from "@/components/ui/button.tsx";
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage,} from "@/components/ui/form.tsx";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue,} from "@/components/ui/select.tsx";
import {useForm} from "react-hook-form";
import {useEffect, useState} from "react";
import FileUpload from "@/components/file-upload/FileUpload";
import {ApplicationStatus,} from "@/generated";
import useFilteredApplications from "@/hooks/useFilteredApplications.ts";

const ITEMS_PER_PAGE = 6;

const ApplicationsView = () => {
    const navigate = useNavigate();
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);

    const form = useForm({
        defaultValues: {
            year: "All",
            status: "All",
            order: "Descending",
        },
    });

    const {control, watch, setValue} = form;

    const yearValue = watch("year");
    const statusValue = watch("status");
    const orderValue = watch("order");


    const {filteredApplications, years, isLoading} = useFilteredApplications(yearValue, statusValue, orderValue);

    useEffect(() => {
        setTotalPages(Math.ceil(filteredApplications.length / ITEMS_PER_PAGE));
        setCurrentPage(1); // Reset to first page when filters change
    }, [filteredApplications]);

    const onPageChange = (page: number) => {
        setCurrentPage(page);
    };

    const paginatedApplications = filteredApplications.slice(
        (currentPage - 1) * ITEMS_PER_PAGE,
        currentPage * ITEMS_PER_PAGE
    );


    return (
        <div>
            <div className={"w-full flex justify-around"}>
                <div className={"w-1/4"}></div>
                <div className={"w-2/3 justify-around flex  gap-2 flex-row"}>
                    <div className="text-3xl font-bold w-full">Applications:</div>
                    <FileUpload/>
                    <Button>
                        <Link to="/proposal">Add application</Link>
                    </Button>
                </div>
                <div className={"w-1/4"}></div>
            </div>

            <div className={"w-full flex justify-around"}>
                <div className={"w-1/4"}>
                    <div className={"w-full h-full pl-5"}>
                        <Form {...form}>
                            <form className="space-y-4">
                                <FormField
                                    control={form.control}
                                    name="year"
                                    render={({field}) => (
                                        <FormItem className="w-40">
                                            <FormLabel>Year</FormLabel>
                                            <Select
                                                onValueChange={field.onChange}
                                                defaultValue={field.value}
                                            >
                                                <FormControl>
                                                    <SelectTrigger>
                                                        <SelectValue placeholder="Select year"/>
                                                    </SelectTrigger>
                                                </FormControl>
                                                <SelectContent>
                                                    <SelectItem value="All">All</SelectItem>
                                                    {years.map((year) => (
                                                        <SelectItem key={year} value={year.toString()}>
                                                            {year}
                                                        </SelectItem>
                                                    ))}
                                                </SelectContent>
                                            </Select>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />

                                <FormField
                                    control={form.control}
                                    name="status"
                                    render={({field}) => (
                                        <FormItem className="w-40">
                                            <FormLabel>Status</FormLabel>
                                            <Select
                                                onValueChange={field.onChange}
                                                defaultValue={field.value}
                                            >
                                                <FormControl>
                                                    <SelectTrigger>
                                                        <SelectValue placeholder="Select status"/>
                                                    </SelectTrigger>
                                                </FormControl>
                                                <SelectContent>
                                                    <SelectItem value="All">All</SelectItem>
                                                    <SelectItem value={ApplicationStatus.PENDING}>
                                                        Pending
                                                    </SelectItem>
                                                    <SelectItem value={ApplicationStatus.DRAFT}>
                                                        Draft
                                                    </SelectItem>
                                                    <SelectItem value={ApplicationStatus.REJECTED}>
                                                        Rejected
                                                    </SelectItem>
                                                    <SelectItem value={ApplicationStatus.ACCEPTED}>
                                                        Accepted
                                                    </SelectItem>
                                                    <SelectItem
                                                        value={ApplicationStatus.CHANGE_REQUESTED}
                                                    >
                                                        Change Requested
                                                    </SelectItem>
                                                </SelectContent>
                                            </Select>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />

                                <FormField
                                    control={form.control}
                                    name="order"
                                    render={({field}) => (
                                        <FormItem className="w-40">
                                            <FormLabel>Order</FormLabel>
                                            <Select
                                                onValueChange={field.onChange}
                                                defaultValue={field.value}
                                            >
                                                <FormControl>
                                                    <SelectTrigger>
                                                        <SelectValue placeholder="Select order"/>
                                                    </SelectTrigger>
                                                </FormControl>
                                                <SelectContent>
                                                    <SelectItem value="Descending">Descending</SelectItem>
                                                    <SelectItem value="Ascending">Ascending</SelectItem>
                                                </SelectContent>
                                            </Select>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />
                            </form>
                        </Form>
                    </div>
                </div>
                <div
                    className={
                        "grid grid-cols-2 gap-4 w-2/3 justify-around mt-4"
                    }
                >
                    {paginatedApplications.map((application) => (
                        <ApplicationCard
                            title={application.title}
                            date={application.createdAt}
                            status={application.status}
                            organisers={application.presenters}
                            onClick={() => navigate(`/proposal-admin/${application.id}`)}
                        />
                    ))}
                </div>
                <div className={"w-1/4"}></div>
            </div>
            <CustomPagination
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={onPageChange}
            />
        </div>
    );
};

export default ApplicationsView;
