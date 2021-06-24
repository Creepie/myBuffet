package com.ada.mybuffet.screens.myShares

data class MySharesModel (val authuid: String = "", val email: String = "") {

    override fun toString(): String {
        return "$authuid $email"
    }
}