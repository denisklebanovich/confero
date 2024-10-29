import ProposalForm from "@/components/proposal/ProposalForm.tsx";

const defaultValues = {
  title: "Computer Vision and Intelligent systems",
  type: "SESSION",
  organisers: [
    { orcid: "0000-0001-2345-6789", name: "Van-Dung Hoang" },
    { orcid: "1234-5678-2345-6789", name: "Kang-Hyun Jo" },
  ],
  description:
    "Computer vision combined with artificial intelligence has been created to better serve the increasing needs of the people. These kinds of computer vision have been applied in a wide range of areas such as surveillance systems, medical diagnosis, intelligent transportation system, and further on cyber-physical interaction systems.",
  tags: ["ObjectDetection", "ImageProcessing"],
};

const ProposalAdminView = () => {
    return (
        <div className='flex flex-col items-center'>
            <div className='text-3xl font-bold pb-5'>Application</div>
            <ProposalForm defaultValues={defaultValues} isDisabled={true}/>
        </div>
    );
};

export default ProposalAdminView;