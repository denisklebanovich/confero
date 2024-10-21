import {RouterProvider} from "react-router-dom";
import {QueryClient, QueryClientProvider} from "@tanstack/react-query";
import {router} from "@/router.tsx";


const queryClient = new QueryClient()

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
        <QueryClientProvider client={queryClient}>
            <RouterProvider router={router}/>
        </QueryClientProvider>
        /*</Auth>*/
    )
}

export default App
