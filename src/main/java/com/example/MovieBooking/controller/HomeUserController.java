package com.example.MovieBooking.controller;

import com.example.MovieBooking.entity.Account;
import com.example.MovieBooking.entity.Movie;
import com.example.MovieBooking.entity.Promotion;
import com.example.MovieBooking.service.IAccountService;
import com.example.MovieBooking.service.IMemberService;
import com.example.MovieBooking.service.IMovieService;
import com.example.MovieBooking.service.IPromotionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeUserController {

    @Autowired
    private IMovieService movieService;

    @Autowired
    private IPromotionService promotionService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IMemberService memberService;


    @GetMapping("/home")
    public String listAllHomeUser(Model model, HttpSession session, @AuthenticationPrincipal Account account) {
        // Retrieve all movies and promotions.
        List<Movie> listMovie = movieService.getAllMovies();
        List<Promotion> listPromotion = promotionService.getAllPromotions();

        // If the user is authenticated, fetch their account information and score.
        if (account != null) {
            Account account1 = accountService.findUserByUsername(account.getUsername());
            Integer score = memberService.getTotalScore(account1.getAccountId());
            session.setAttribute("account", account1);
            session.setAttribute("score", score);
        }

        // Add the movie list and promotion list to the model.
        model.addAttribute("listMovie", listMovie);
        model.addAttribute("listPromotion", listPromotion);

        // Store the movie list in the session.
        session.setAttribute("movieList", listMovie);

        // Return the view name for the user homepage.
        return "homepageUser";
    }
}
