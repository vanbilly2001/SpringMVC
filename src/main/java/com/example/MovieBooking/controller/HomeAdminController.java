package com.example.MovieBooking.controller;

import com.example.MovieBooking.entity.Booking;
import com.example.MovieBooking.entity.Movie;
import com.example.MovieBooking.entity.Promotion;
import com.example.MovieBooking.service.IBookingService;
import com.example.MovieBooking.service.IMovieService;
import com.example.MovieBooking.service.IPromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Controller
public class HomeAdminController {

    @Autowired
    private IMovieService movieService;

    @Autowired
    private IPromotionService promotionService;

    @Autowired
    private IBookingService bookingService;

    @GetMapping("/homeAdmin")
    public String homeAdmin(Model model) {
        // Retrieve all movies, promotions, and bookings.
        List<Movie> listMovie = movieService.getAllMovies();
        List<Promotion> listPromotion = promotionService.getAllPromotions();
        List<Booking> listBooking = bookingService.findAll();

        // Calculate the counts of each entity.
        int countMovie = listMovie.size();
        int countPromotion = listPromotion.size();
        int countBooking = listBooking.size();

        // Add the data and counts to the model.
        model.addAttribute("listMovie", listMovie);
        model.addAttribute("countMovie", countMovie);
        model.addAttribute("countBooking", countBooking);
        model.addAttribute("countPromotion", countPromotion);

        // Return the view name for the admin homepage.
        return "homepageAdmin";
    }
}
