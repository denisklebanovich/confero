import {createBrowserRouter} from "react-router-dom";
import Layout from "@/components/layout/Layout.tsx";
import SessionsView from "@/views/SessionsView.tsx";
import ProposalView from "@/views/ProposalView.tsx";
import LoginView from "@/views/LoginView.tsx";
import ApplicationsView from "@/views/ApplicationsView.tsx";
import SessionView from "@/views/SessionView.tsx";
import AdminSessionView from "@/views/AdminSessionView.tsx";
import TimetableSessionView from "@/views/TimetableSessionView.tsx";
import {AuthProvider} from "@/auth/AuthProvider.tsx";
import ProposalEditView from "./views/ProposalEditView";
import ProposalAdminView from "./views/ProposalAdminView";
import MyCalendarView from "@/views/MyCalendarView.tsx";
import MySessionsView from "@/views/MySessionsView.tsx";
import OrganisersView from "@/views/OrganisersView.tsx";
import ConferenceEditionsView from "@/views/ConferenceEditionsView.tsx";
import {Toaster} from "@/components/ui/toaster.tsx";
import React from "react";
import SignUpView from "@/views/SignUpView.tsx";

export const router = createBrowserRouter([
    {
        path: '/',
        element: (
            <>
                <AuthProvider>
                    <Toaster/>
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
                path: 'proposal-edit/:id',
                element: <ProposalEditView/>
            },
            {
                path: 'applications',
                element: <ApplicationsView/>
            },
            {
                path: `session/:id`,
                element: <SessionView/>
            },
            {
                path: 'admin-sessions',
                element: <AdminSessionView/>
            },
            {
                path: `proposal-admin/:id`,
                element: <ProposalAdminView/>
            },
            {
                path: 'timetable',
                element: <TimetableSessionView/>
            },
            {
                path: 'my-calendar',
                element: <MyCalendarView/>
            },
            {
                path: 'my-sessions',
                element: <MySessionsView/>
            },
            {
                path: 'organizers',
                element: <OrganisersView/>
            }
            ,
            {
                path: 'conference-editions',
                element: <ConferenceEditionsView/>
            }
        ]
    },
    {
        path: '/login',
        element: <>
            <AuthProvider>
                <Toaster/>
                <LoginView/>
            </AuthProvider>
        </>
    },
    {
        path: '/signup',
        element: <>
            <AuthProvider>
                <Toaster/>
                <SignUpView/>
            </AuthProvider>
        </>
    }
])