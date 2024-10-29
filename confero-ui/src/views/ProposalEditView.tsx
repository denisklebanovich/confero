import ApplicationComment from "@/components/admin-application/ApplicationComment";
import ProposalForm from "@/components/proposal/ProposalForm.tsx";

const defaultValues = {
  title: "Computer Vision and Intelligent systems",
  type: "SESSION",
  organisers: [
    { orcid: "0000-0001-2345-6789", name: "Van-Dung Hoang" },
    { orcid: "1234-5678-2345-6789", name: "Kang-Hyun Jo" },
    { orcid: "1234-5678-2345-6789", name: "Dinh-Hien Nguyen" },
    { orcid: "1234-5678-2345-6789", name: "Huyen Trang Phan" },
  ],
  description:
    "Computer vision combined with artificial intelligence has been created to better serve the increasing needs of the people. These kinds of computer vision have been applied in a wide range of areas such as surveillance systems, medical diagnosis, intelligent transportation system, and further on cyber-physical interaction systems.",
  tags: ["ObjectDetection", "ImageProcessing"],
};

const comments = [
  {
    user: "Admin",
    comment: "Change the objective. The type of the event is wrong.",
  },
  {
    user: "Admin",
    comment:
      "Update the timeline. Ensure all dates align with the new objectives.",
  },
];

const ProposalEditView = () => {

  return (
    <div className="relative flex items-start">
      <div className="flex flex-col w-full items-center">
        <div className="text-3xl font-bold pb-5">Edit application</div>
        <ProposalForm defaultValues={defaultValues} />
      </div>

      <div className="absolute top-0 right-0 h-full flex flex-col items-end p-4 space-y-4 z-50">
        {comments.map((comment, index) => (
          <ApplicationComment key={index} {...comment} />
        ))}
      </div>
    </div>
  );
};

export default ProposalEditView;
