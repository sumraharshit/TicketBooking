package com.booking.ticketBooking.event.dto;

import com.booking.ticketBooking.event.entity.LineUp;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record PerformerDto(@Nullable Set<LineUp> lineUps, @NotNull @NotEmpty String name, @NotNull @NotEmpty String category)
{
}
