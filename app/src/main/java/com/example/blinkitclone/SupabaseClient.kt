package com.example.blinkitclone

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {

    val client = createSupabaseClient(
        supabaseUrl = "https://wpsqmolucmkbxlnppris.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Indwc3Ftb2x1Y21rYnhsbnBwcmlzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODAyMTUxNzMsImV4cCI6MjA5NTc5MTE3M30.GgiSKSpMBOJBQ8h8P9wUoiWeWjAtJbKsQWrQI86cncw"
    ) {
        install(Auth)
        install(Postgrest)
    }
}
