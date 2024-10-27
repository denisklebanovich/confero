import { Separator } from "@/components/ui/separator"

const Footer = () => {
    return (
        <footer className="text-white bg-white text-center pb-4 pt-4 fixed bottom-0 w-full">
            <Separator orientation="horizontal" className="mb-4" />
            <div className="text-sm text-gray-700">
                © 2024 Confero. All rights reserved.
            </div>
        </footer>
    );
};

export default Footer;