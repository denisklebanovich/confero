import CustomPagination from "@/components/application/CustomPagination.tsx";
import ApplicationCard from "@/components/application/ApplicationCard.tsx";
import { Link, useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button.tsx";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form.tsx";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select.tsx";
import { useForm } from "react-hook-form";
import { useEffect, useState } from "react";
import FileUpload from "@/components/file-upload/FileUpload";
import { ApplicationPreviewResponse, ApplicationStatus, SessionType } from "@/generated";

const ApplicationView = () => {
  const navigate = useNavigate();
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(10);

  const mockApplications: ApplicationPreviewResponse[] = [
    {
      id: 1,
      title: "Innovative Approaches in Quantum Computing",
      type: SessionType.SESSION,
      presenters: [
        {
          id: 101,
          name: "Dr. Alice",
          surname: "Smith",
          orcid: "0000-0001-2345-6789",
          isSpeaker: true,
        },
        {
          id: 102,
          name: "Dr. Bob",
          surname: "Johnson",
          orcid: "0000-0002-2345-6789",
          isSpeaker: false,
        },
      ],
      createdAt: "2023-05-01T10:30:00Z",
      status: ApplicationStatus.PENDING,
      from_active_conference_edition: true,
    },
    {
      id: 2,
      title: "Artificial Intelligence in Healthcare",
      type: SessionType.TUTORIAL,
      presenters: [
        {
          id: 103,
          name: "Prof. Charles",
          surname: "Lee",
          orcid: "0000-0003-2345-6789",
          isSpeaker: true,
        },
      ],
      createdAt: "2023-09-15T15:00:00Z",
      status: ApplicationStatus.CHANGE_REQUESTED,
      from_active_conference_edition: false,
    },
    {
      id: 3,
      title: "Sustainability Practices in Urban Development",
      type: SessionType.TUTORIAL,
      presenters: [
        {
          id: 104,
          name: "Dr. Emma",
          surname: "Brown",
          orcid: "0000-0004-2345-6789",
          isSpeaker: true,
        },
        {
          id: 105,
          name: "Mr. David",
          surname: "Green",
          orcid: "0000-0005-2345-6789",
          isSpeaker: false,
        },
      ],
      createdAt: "2023-11-10T12:00:00Z",
      status: ApplicationStatus.DRAFT,
      from_active_conference_edition: true,
    },
    {
      id: 4,
      title: "The Future of Renewable Energy",
      type: SessionType.TUTORIAL,
      presenters: [
        {
          id: 106,
          name: "Dr. Frank",
          surname: "White",
          orcid: "0000-0006-2345-6789",
          isSpeaker: true,
        },
      ],
      createdAt: "2024-02-20T09:00:00Z",
      status: ApplicationStatus.PENDING,
      from_active_conference_edition: true,
    },
    {
      id: 5,
      title: "Advancements in Space Exploration",
      type: SessionType.TUTORIAL,
      presenters: [
        {
          id: 107,
          name: "Dr. Grace",
          surname: "Black",
          orcid: "0000-0007-2345-6789",
          isSpeaker: true,
        },
        {
          id: 108,
          name: "Dr. Henry",
          surname: "Gold",
          orcid: "0000-0008-2345-6789",
          isSpeaker: false,
        },
      ],
      createdAt: "2024-06-15T14:00:00Z",
      status: ApplicationStatus.REJECTED,
      from_active_conference_edition: false,
    },
    {
      id: 6,
      title: "Cybersecurity Trends in 2024",
      type: SessionType.SESSION,
      presenters: [
        {
          id: 109,
          name: "Dr. Iris",
          surname: "Gray",
          orcid: "0000-0009-2345-6789",
          isSpeaker: true,
        },
      ],
      createdAt: "2024-08-05T16:30:00Z",
      status: ApplicationStatus.PENDING,
      from_active_conference_edition: true,
    },
  ];

  function onPageChange(page) {
    setCurrentPage(page);
  }

  const form = useForm({
    defaultValues: {
      year: "All",
      status: "All",
      order: "Descending",
    },
  });

  const { control, watch, setValue } = form;

  const yearValue = watch("year");
  const statusValue = watch("status");
  const orderValue = watch("order");

  useEffect(() => {
    console.log(yearValue, statusValue, orderValue);
  }, [yearValue, statusValue, orderValue]);

  return (
    <div>
      <div className={"w-full flex justify-around"}>
        <div className={"w-1/4"}></div>
        <div className={"w-2/3 justify-around flex  gap-2 flex-row"}>
          <div className="text-3xl font-bold w-full">Applications:</div>
          <FileUpload />
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
                  render={({ field }) => (
                    <FormItem className="w-40">
                      <FormLabel>Year</FormLabel>
                      <Select
                        onValueChange={field.onChange}
                        defaultValue={field.value}
                      >
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
                      <Select
                        onValueChange={field.onChange}
                        defaultValue={field.value}
                      >
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
                      <Select
                        onValueChange={field.onChange}
                        defaultValue={field.value}
                      >
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
        <div
          className={
            "grid grid-cols-2 mt-5 gap-y-4 gap-x-4 w-2/3 overflow-auto max-h-[55vh] px-2"
          }
        >
          {mockApplications.map((application) => (
            <ApplicationCard
              title={application.title}
              date={application.createdAt}
              status={application.status}
              organisers={application.presenters}
              onClick={() => navigate("/proposal-edit")}
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

export default ApplicationView;
