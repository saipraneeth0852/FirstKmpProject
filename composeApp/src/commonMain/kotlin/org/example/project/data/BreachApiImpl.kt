package org.example.project.data

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.*
import org.example.project.domain.Breach
import android.util.Log

class ApiException(message: String, cause: Throwable? = null) : Exception(message, cause)

interface BreachApi {
    suspend fun getBreaches(): List<Breach>
}

class BreachApiImpl : BreachApi {
    private val client = HttpClient()
    private val TAG = "BreachApiImpl"

    override suspend fun getBreaches(): List<Breach> {
        return try {
            Log.d(TAG, "Attempting to fetch breaches from API")
            val response: HttpResponse = client.get("https://haveibeenpwned.com/api/v3/breaches")
            val jsonString = response.bodyAsText()
            Log.d(TAG, "Raw API Response: $jsonString")

            val jsonArray = Json.parseToJsonElement(jsonString).jsonArray
            Log.d(TAG, "Parsed JSON Array size: ${jsonArray.size}")

            jsonArray.mapIndexed { index, jsonElement ->
                val jsonObject = jsonElement.jsonObject
                Log.d(TAG, "Parsing breach $index: ${jsonObject["Name"]?.jsonPrimitive?.content}")
                Breach(
                    name = jsonObject["Name"]?.jsonPrimitive?.content ?: "",
                    title = jsonObject["Title"]?.jsonPrimitive?.content ?: "",
                    domain = jsonObject["Domain"]?.jsonPrimitive?.content ?: "",
                    breachDate = jsonObject["BreachDate"]?.jsonPrimitive?.content ?: "",
                    addedDate = jsonObject["AddedDate"]?.jsonPrimitive?.content ?: "",
                    modifiedDate = jsonObject["ModifiedDate"]?.jsonPrimitive?.content ?: "",
                    pwnCount = jsonObject["PwnCount"]?.jsonPrimitive?.int ?: 0,
                    description = jsonObject["Description"]?.jsonPrimitive?.content ?: "",
                    logoPath = jsonObject["LogoPath"]?.jsonPrimitive?.content ?: "",
                    dataClasses = jsonObject["DataClasses"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList(),
                    isVerified = jsonObject["IsVerified"]?.jsonPrimitive?.boolean ?: false,
                    isFabricated = jsonObject["IsFabricated"]?.jsonPrimitive?.boolean ?: false,
                    isSensitive = jsonObject["IsSensitive"]?.jsonPrimitive?.boolean ?: false,
                    isRetired = jsonObject["IsRetired"]?.jsonPrimitive?.boolean ?: false,
                    isSpamList = jsonObject["IsSpamList"]?.jsonPrimitive?.boolean ?: false,
                    isMalware = jsonObject["IsMalware"]?.jsonPrimitive?.boolean ?: false,
                    isSubscriptionFree = jsonObject["IsSubscriptionFree"]?.jsonPrimitive?.boolean ?: false
                )
            }.also {
                Log.d(TAG, "Successfully parsed ${it.size} breaches")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in getBreaches: ${e.message}")
            e.printStackTrace()
            throw ApiException("Failed to fetch breaches: ${e.message}", e)
        }
    }
}