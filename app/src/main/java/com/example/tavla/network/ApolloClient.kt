package com.example.tavla.network

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.okHttpClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object ApolloClient {
    private const val baseUrl= "https://api.entur.io/journey-planner/v3/graphql"

    private val okHttpClient = OkHttpClient.Builder()

        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val apolloClient: ApolloClient = ApolloClient.Builder()
        .serverUrl(baseUrl)
        .okHttpClient(okHttpClient) // Attach OkHttpClient
        .addHttpHeader("Content-Type", "application/json")
        .addHttpHeader("ET-Client-Name", "jonashagelid-tavla")
        .build()
}
