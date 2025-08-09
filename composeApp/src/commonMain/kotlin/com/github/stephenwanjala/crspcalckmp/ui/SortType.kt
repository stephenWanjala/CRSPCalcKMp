package com.github.stephenwanjala.crspcalckmp.ui

sealed class SortType(val orderType: OrderType) {
    class Make(currentOrderType: OrderType) : SortType(currentOrderType)
    class Price(currentOrderType: OrderType) : SortType(currentOrderType)
    class Seats(currentOrderType: OrderType) : SortType(currentOrderType)

    fun copy(newOrderType: OrderType): SortType {
        return when (this) {
            is Make -> Make(newOrderType)
            is Price -> Price(newOrderType)
            is Seats -> Seats(newOrderType)
        }
    }
}