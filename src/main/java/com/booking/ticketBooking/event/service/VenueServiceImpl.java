package com.booking.ticketBooking.event.service;

import com.booking.ticketBooking.event.dto.VenueDto;

import com.booking.ticketBooking.event.entity.Venue;

import com.booking.ticketBooking.event.repository.VenueRepository;
import com.booking.ticketBooking.event.utility.VenueMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VenueServiceImpl implements VenueService{

    private final VenueRepository venueRepository;
    private final VenueMapper venueMapper;

    @Override
    public VenueDto viewVenue(Long id) {
        Venue venue = venueRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("The Venue does not exits for the id: " + id)
        );

        return venueMapper.toDto(venue);
    }

    @Override
    public String addVenue(VenueDto venueDto) {
        Venue venue = venueMapper.toEntity(venueDto);

        venueRepository.save(venue);

        return "The venue Has been added";
    }

    @Override
    public void deleteVenue(Long id) {

    }
    
}
