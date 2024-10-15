import {Auth} from "@supabase/auth-ui-react";
import {supabase} from "@/auth/supabaseClient.ts";
import {ThemeSupa} from "@supabase/auth-ui-shared";

function App() {
    return (
        <Auth supabaseClient={supabase} appearance={{theme: ThemeSupa}}
              providers={['linkedin_oidc']}
              onlyThirdPartyProviders
              localization={{
                  variables: {
                      sign_in:{
                          social_provider_text: 'Zaloguj się przez LinkedIn'
                      }
                  }
              }}
        >
            <div>Confero UI</div>
        </Auth>
    )
}

export default App
