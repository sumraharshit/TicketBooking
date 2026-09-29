package com.booking.ticketBooking.event.service;

import com.booking.ticketBooking.event.dto.PerformerDto;
import com.booking.ticketBooking.event.entity.Performer;
import com.booking.ticketBooking.event.repository.PerfomerRepository;
import com.booking.ticketBooking.event.utility.PerformerMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PerfomerServiceImpl implements PerformerService{

    private final PerfomerRepository perfomerRepository;
    private final PerformerMapper performerMapper;

    @Override
    public PerformerDto viewPerformer(Long id) {
      Performer performer = perfomerRepository.findById(id).orElseThrow(
              () -> new IllegalArgumentException("The Perfomer does not exits for the id: " + id)
      );

      return performerMapper.toDto(performer);
    }

    @Override
    public String addPerformer(PerformerDto performerDto) {
        Performer performer = performerMapper.toEntity(performerDto);

        perfomerRepository.save(performer);

        return "The Performer Has been added";
    }

    @Override
    public void deletePerformer(Long id) {

    }
}
