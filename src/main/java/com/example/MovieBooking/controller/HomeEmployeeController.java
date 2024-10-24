package com.example.MovieBooking.controller;

import com.example.MovieBooking.entity.Booking;
import com.example.MovieBooking.entity.Member;
import com.example.MovieBooking.service.IBookingService;
import com.example.MovieBooking.service.IMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Controller
public class HomeEmployeeController {

    @Autowired
    private IMemberService memberService;

    @Autowired
    private IBookingService bookingService;


    @GetMapping("/homeEmployee")
    public String homeEmployee(Model model) {
        // Retrieve all members and bookings.
        List<Member> members = memberService.getAllMembers();
        List<Booking> listBooking = bookingService.findAll();

        // Calculate the counts of members and bookings.
        int countBooking = listBooking.size();
        int countMember = members.size();

        // Add the data and counts to the model.
        model.addAttribute("listMember", members);
        model.addAttribute("countBooking", countBooking);
        model.addAttribute("countMember", countMember);

        // Return the view name for the employee homepage.
        return "homepageEmployee";
    }
}
