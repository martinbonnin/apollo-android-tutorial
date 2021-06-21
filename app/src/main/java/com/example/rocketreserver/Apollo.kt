package com.example.rocketreserver

import android.content.Context
import android.os.Looper
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.network.http.HttpNetworkTransport
import com.apollographql.apollo3.network.ws.WebSocketNetworkTransport
import com.apollographql.apollo3.rx2.*
import io.reactivex.Single

private var instance: Rx2ApolloClient? = null

fun apolloClient(context: Context): Rx2ApolloClient {
    check(Looper.myLooper() == Looper.getMainLooper()) {
        "Only the main thread can get the apolloClient instance"
    }

    if (instance != null) {
        return instance!!
    }

    val client = ApolloClient(
        networkTransport = HttpNetworkTransport(
            serverUrl = "https://apollo-fullstack-tutorial.herokuapp.com/graphql",
            interceptors = listOf(AuthorizationInterceptor(context).toHttpInterceptor())
        ),
        subscriptionNetworkTransport = WebSocketNetworkTransport("wss://apollo-fullstack-tutorial.herokuapp.com/graphql")
    )
    instance = client.toRx2ApolloClient()

    return instance!!
}

private class AuthorizationInterceptor(val context: Context) : Rx2HttpInterceptor {

    override fun intercept(
        request: HttpRequest,
        chain: Rx2HttpInterceptorChain
    ): Single<HttpResponse> {
        val newHeaders = request.headers + ("Authorization" to (User.getToken(context) ?: ""))
        return chain.proceed(request.copy(headers = newHeaders))
    }
}
