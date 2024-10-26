import ProposalForm from "@/components/proposal/ProposalForm.tsx";

const ProposalView = () => {
    return (
        <div className='flex flex-col items-center'>
            <div className='text-3xl font-bold pb-5'>Create new application</div>
            <ProposalForm/>
        </div>
    );
};

export default ProposalView;