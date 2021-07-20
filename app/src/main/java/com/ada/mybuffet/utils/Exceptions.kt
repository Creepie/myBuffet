package com.ada.mybuffet.utils

class InvalidSalePurchaseBalanceException(message: String) : Exception(message)
class StockNotFound(message: String) : Exception(message)
class DatabaseException(message: String) : Exception(message)