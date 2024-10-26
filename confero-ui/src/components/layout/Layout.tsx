import Navbar from "@/components/layout/Navbar.tsx";
import {Outlet} from "react-router-dom";
import Footer from "@/components/layout/Footer.tsx";

const Layout = () => {
    return (
        <div className={"min-w-screen min-h-screen"}>
            <Navbar/>
            <div className="flex-grow px-8 py-24 overflow-y-auto">
                <Outlet/>
            </div>
            <Footer/>
        </div>
    );
};

export default Layout;