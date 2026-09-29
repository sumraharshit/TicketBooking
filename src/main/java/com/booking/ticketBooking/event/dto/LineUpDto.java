package com.booking.ticketBooking.event.dto;

import jakarta.validation.constraints.NotNull;

public record LineUpDto(@NotNull Long perfomerId) {
}
