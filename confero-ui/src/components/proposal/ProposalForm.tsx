import {LabelInput} from "@/components/ui/labelInput.tsx";

const ProposalForm = () => {
    return (
        <form className='flex flex-col items-start'>
            <LabelInput label='Title'/>
            <LabelInput label='Description'/>
        </form>
    );
};

export default ProposalForm;