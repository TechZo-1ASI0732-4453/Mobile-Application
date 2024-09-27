package com.techzo.cambiazo.data.remote
/*
{
  "username": "string",
  "password": "string",
  "name": "string",
  "phoneNumber": "string",
  "profilePicture": "string",
  "roles": [
    "string"
  ]
}
 */
data class SignUpRequestDto(
    val username: String,
    val password: String,
    val name: String,
    val phoneNumber: String,
    val profilePicture: String,
    val roles: List<String>
)
