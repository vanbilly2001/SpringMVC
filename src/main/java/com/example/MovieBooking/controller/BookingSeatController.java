package com.example.MovieBooking.controller;

import com.example.MovieBooking.dto.req.AccountDTO;
import com.example.MovieBooking.entity.*;
import com.example.MovieBooking.service.IMemberService;
import com.example.MovieBooking.service.IShowDateService;
import com.example.MovieBooking.service.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BookingSeatController {

    @Autowired
    private CinemaRoomServiceImpl cinemaRoomService;

    @Autowired
    private SeatServiceImpl seatService;

    @Autowired
    private MovieServiceImpl movieService;

    @Autowired
    private BookingSeatServiceImpl bookingSeatService;

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private ScheduleServiceImpl scheduleService;

    @Autowired
    private AccountServiceImpl accountService;

    @Autowired
    private IMemberService memberService;

    @Autowired
    private IShowDateService showDateService;

    @GetMapping("/select-seat")
    public String showSelectSeat(
            @RequestParam("movieId") Long movieId,
            @RequestParam("scheduleId") Long scheduleId,
            @RequestParam(value = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateSelect,
            Model model) {
        // Retrieves the movie and its cinema room
        Movie movie = movieService.getMovieById(movieId);
        Long cinemaRoomId = movie.getCinemaRoom().getCinemaRoomId();

        // Retrieves available seats in the cinema room
        List<Seat> seats = seatService.listSeatByCinemaRoomId(cinemaRoomId);

        // Retrieves seats that are already booked for the selected movie and schedule
        List<BookingSeat> bookingSeats = bookingSeatService.getBookedSeats(movieId, scheduleId, dateSelect);

        // Marks booked seats as 'sold'
        for (BookingSeat bookingSeat : bookingSeats) {
            for (Seat seat : seats) {
                if (seat.getSeatId().equals(bookingSeat.getSeat().getSeatId())) {
                    seat.setSeatStatus(1); // Assuming status 1 means 'booked'
                }
            }
        }

        // Counts available seats
        int availableSeatsCount = (int) seats.stream().filter(seat -> seat.getSeatStatus() == 0).count();

        // Creates a list of selectable seat quantities
        List<Integer> seatQuantities = new ArrayList<>();
        for (int i = 1; i <= availableSeatsCount; i++) {
            seatQuantities.add(i);
        }

        // Groups seats into rows for display
        List<List<Seat>> rows = new ArrayList<>();
        for (int i = 0; i < seats.size(); i += 6) {
            int end = Math.min(i + 6, seats.size());
            rows.add(seats.subList(i, end));
        }

        // Adds relevant data to the model
        model.addAttribute("seats", bookingSeats);
        model.addAttribute("availableSeatsCount", availableSeatsCount);
        model.addAttribute("seatQuantities", seatQuantities);
        model.addAttribute("seatRows", rows);
        model.addAttribute("movieId", movieId);
        model.addAttribute("scheduleId", scheduleId);
        model.addAttribute("date", dateSelect);

        return "TKS-selecttingseat";
    }

    @GetMapping("/confirm")
    public String confirmBooking(@RequestParam("selectedSeat") Long[] selectedSeats,
                                 @RequestParam("movieId") Long movieId,
                                 @RequestParam("scheduleId") Long scheduleId,
                                 @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateconfirm,
                                 Model model) {
        Movie movie = movieService.getMovieById(movieId);
        Schedule schedule = scheduleService.getScheduleById(scheduleId);
        Long cinemaRoomId = movie.getCinemaRoom().getCinemaRoomId();

        List<Seat> seatDetails = new ArrayList<>();
        for (Long seatID : selectedSeats) {
            Seat seat = seatService.getSeatById(seatID);
            seatDetails.add(seat);
        }

        // Calculates the total price for selected seats
        int sum = seatDetails.stream().mapToInt(seat -> Math.toIntExact(seat.getSeatType().getPrice())).sum();

        // Adds relevant data to the model
        model.addAttribute("total", sum);
        model.addAttribute("selectedSeats", selectedSeats);
        model.addAttribute("movie", movie);
        model.addAttribute("date", dateconfirm);
        model.addAttribute("schedule", schedule);
        model.addAttribute("seatDetails", seatDetails);

        return "TKS-confirmbookingticket";
    }

    @PostMapping("/infor")
    public String showInforTicket(@RequestParam("movieId") Long movieId,
                                  @RequestParam(value = "memberId", required = false) Long memberId,
                                  @RequestParam("scheduleId") Long scheduleId,
                                  @RequestParam("seat") List<Long> listSeats,
                                  @RequestParam("sum") Long totalMoney,
                                  @RequestParam("date") String dateconfirm,
                                  @RequestParam(value = "Agree") String isAgree,
                                  Model model) {
        // Logic to save the booking and handle membership points
        // ...

        return "/TKS-ticketinfomation";
    }

    @GetMapping("/getMember")
    @ResponseBody
    public AccountDTO getMember(@RequestParam("memberInput") Long id,
                                @RequestParam(value = "Agree", defaultValue = "false") boolean isAgree,
                                @RequestParam("total") int total,
                                @RequestParam("score") double currentScore,
                                Model model) {
        model.addAttribute("agree", isAgree);
        return memberService.getMember(id);
    }
}
