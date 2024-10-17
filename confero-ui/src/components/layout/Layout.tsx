import Navbar from "@/components/layout/Navbar.tsx";
import {Outlet} from "react-router-dom";

const Layout = () => {
    return (
        <div>
            <Navbar/>
            <div className="px-8 py-4">
                <Outlet/>
            </div>
        </div>
    );
};

export default Layout;