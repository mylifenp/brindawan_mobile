package com.brindawan.mobile.data

import com.apollographql.apollo.ApolloClient
import com.brindawan.mobile.CreateContactMutation
import com.brindawan.mobile.type.ContactInput

class ContactRepository(
    private val apolloClient: ApolloClient,
) {
    suspend fun createContact(
        name: String,
        email: String,
        phone: String,
        message: String,
    ) {
        val response = apolloClient.mutation(
            CreateContactMutation(
                contactInput = ContactInput(
                    email = email,
                    message = message,
                    name = name,
                    phone = phone,
                ),
            ),
        ).execute()

        if (response.hasErrors()) {
            val errorMessage = response.errors?.joinToString { it.message } ?: "Unknown GraphQL error"
            throw IllegalStateException(errorMessage)
        }

        if (response.data?.createContact?.id.isNullOrBlank()) {
            throw IllegalStateException("Failed to send message")
        }
    }
}


