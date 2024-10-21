import {createBrowserRouter} from "react-router-dom";
import Layout from "@/components/layout/Layout.tsx";
import {Home} from "lucide-react";
import SessionsView from "@/views/SessionsView.tsx";
import ProposalView from "@/views/ProposalView.tsx";

export const router = createBrowserRouter([
    {
        path: '/',
        element: (
            <>
                <Layout/>
            </>
        ),
        children: [
            {
                index: true,
                element: <Home/>
            },
            {
                path: 'sessions',
                element: <SessionsView/>
            },
            {
                path: 'proposal',
                element: <ProposalView/>
            }
        ]
    }
])