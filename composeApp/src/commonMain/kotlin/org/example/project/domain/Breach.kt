package org.example.project.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Breach(
    val name: String,
    val title: String,
    val domain: String,
    @SerialName("BreachDate") val breachDate: String,
    @SerialName("AddedDate") val addedDate: String,
    @SerialName("ModifiedDate") val modifiedDate: String,
    @SerialName("PwnCount") val pwnCount: Int,
    val description: String,
    @SerialName("LogoPath") val logoPath: String,
    @SerialName("DataClasses") val dataClasses: List<String>,
    @SerialName("IsVerified") val isVerified: Boolean,
    @SerialName("IsFabricated") val isFabricated: Boolean,
    @SerialName("IsSensitive") val isSensitive: Boolean,
    @SerialName("IsRetired") val isRetired: Boolean,
    @SerialName("IsSpamList") val isSpamList: Boolean,
    @SerialName("IsMalware") val isMalware: Boolean,
    @SerialName("IsSubscriptionFree") val isSubscriptionFree: Boolean
)