import { createClient } from '@supabase/supabase-js';

const supabaseUrl = 'https://yerxopcahxybrxkyxayf.supabase.co';
const supabaseAnonKey = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InllcnhvcGNhaHh5YnJ4a3l4YXlmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mjg5OTU0MTMsImV4cCI6MjA0NDU3MTQxM30.3PWhNvm0riw5JRzAVM5d2w0A1e996rjE0qsNTpaN0lM';

export const supabase = createClient(supabaseUrl, supabaseAnonKey);


async function signInWithLinkedIn() {
    const { data, error } = await supabase.auth.signInWithOAuth({
        provider: 'linkedin_oidc',
    })
}
