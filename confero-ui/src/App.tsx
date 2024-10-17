import {Route, Routes} from "react-router-dom";
import ProposalView from "@/views/ProposalView.tsx";
import Layout from "@/components/layout/Layout.tsx";

function App() {
    return (
        // <Auth supabaseClient={supabase} appearance={{theme: ThemeSupa}}
        //       providers={['linkedin_oidc']}
        //       onlyThirdPartyProviders
        //       localization={{
        //           variables: {
        //               sign_in: {
        //                   social_provider_text: 'Zaloguj się przez LinkedIn'
        //               }
        //           }
        //       }}
        // >
        <Routes>
            <Route path='/' element={<Layout/>}>
                <Route index element={<ProposalView/>}/>
            </Route>
        </Routes>
        /*</Auth>*/
    )
}

export default App
