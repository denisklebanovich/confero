import {createBrowserRouter} from "react-router-dom";
import Layout from "@/components/layout/Layout.tsx";
import SessionsView from "@/views/SessionsView.tsx";
import ProposalView from "@/views/ProposalView.tsx";
import LoginView from "@/views/LoginView.tsx";
import ApplicationView from "@/views/ApplicationView.tsx";
import SessionView from "@/views/SessionView.tsx";
import AdminSessionView from "@/views/AdminSessionView.tsx";
import TimetableSessionView from "@/views/TimetableSessionView.tsx";
import {AuthProvider} from "@/auth/AuthProvider.tsx";

export const router = createBrowserRouter([
    {
        path: '/',
        element: (
            <>
                <AuthProvider>
                    <Layout/>
                </AuthProvider>
            </>
        ),
        children: [
            {
                index: true,
                element: <SessionsView/>
            },
            {
                path: 'proposal',
                element: <ProposalView/>
            },
            {
                path: 'applications',
                element: <ApplicationView/>
            },
            {
                path: 'session',
                element: <SessionView/>
            },
            {
                path: 'admin-sessions',
                element: <AdminSessionView/>
            },
            {
                path: 'timetable',
                element: <TimetableSessionView/>
            }
        ]
    },
    {
        path: '/login',
        element: <>
            <AuthProvider>
                <LoginView/>
            </AuthProvider>
        </>
    }
])