import ProposalForm from "@/components/proposal/ProposalForm.tsx";

const ProposalView = () => {
    return (
        <div className='flex flex-col items-center'>
            <div className='text-2xl font-bold'>Create new application</div>
            <ProposalForm/>
        </div>
    );
};

export default ProposalView;