import SessionTimeSetter from "@/components/admin-session/SessionTimeSetter.tsx";

const AdminSessionView = () => {
    return (
    <div className={'min-w-full min-h-full'}>
        <div className='flex w-full justify-center'>
            <div className={"w-3/4 items-center gap-5 flex flex-col"}>
                <div className='text-3xl font-bold w-full'>Sessions:</div>
                <SessionTimeSetter/>
                <SessionTimeSetter/>
                <SessionTimeSetter/>
                <SessionTimeSetter/>
        </div>
        </div>
    </div>)
};

export default AdminSessionView;