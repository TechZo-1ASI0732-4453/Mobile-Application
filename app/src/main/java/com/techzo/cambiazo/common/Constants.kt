package com.techzo.cambiazo.common

import com.techzo.cambiazo.domain.FilterValues
import com.techzo.cambiazo.domain.Subscription
import com.techzo.cambiazo.domain.UserSignIn

object Constants {
    const val BASE_URL = "https://cambiazo-techzo-gzdtcfcca4fxeaec.westus-01.azurewebsites.net/api/v2/"
    const val DEFAULT_PROFILE_PICTURE = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR6lqpQj3oAmc1gtyM78oJCbTaDrD7Fj9NRlceOPDZiHA&s"
    const val DEFAULT_ROLE = "ROLE_USER"
    var token: String? = null
    var user: UserSignIn? = null
    var userSubscription: Subscription? = null
    var selectedPlanId: Int? = null
    val filterValues = FilterValues()
}
