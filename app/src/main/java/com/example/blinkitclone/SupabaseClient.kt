package com.example.blinkitclone

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {

    val client = createSupabaseClient(
        supabaseUrl = "Add your supabse project url",
        supabaseKey = "Add your supabase project key"
    ) {
        install(Auth)
        install(Postgrest)
    }
}
