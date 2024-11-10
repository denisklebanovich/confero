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
import CommentView from "@/views/CommentView";
import ProposalEditView from "./views/ProposalEditView";
import ProposalAdminView from "./views/ProposalAdminView";
import MyCalendarView from "@/views/MyCalendarView.tsx";
import MySessionsView from "@/views/MySessionsView.tsx";

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
                path: 'proposal-edit',
                element: <ProposalEditView/>
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
                path: 'admin-proposal',
                element: <ProposalAdminView/>
            },
            {
                path: 'timetable',
                element: <TimetableSessionView/>
            },
            {
                path: 'comment',
                element: <CommentView/>
            },
            {
                path: 'my-calendar',
                element: <MyCalendarView/>
            },
            {
                path: 'my-sessions',
                element: <MySessionsView/>
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