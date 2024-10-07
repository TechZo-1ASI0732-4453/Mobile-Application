package com.techzo.cambiazo.data.remote.signup

import com.techzo.cambiazo.domain.UserSignUp

/*
{
  "id": 0,
  "username": "string",
  "roles": [
    "string"
  ]
}
 */

data class SignUpResponseDto(
    val id: Int,
    val username: String,
    val roles: List<String>
)

fun SignUpResponseDto.toUserSignUp() = UserSignUp(
    id = id,
    username = username,
    roles = roles
)