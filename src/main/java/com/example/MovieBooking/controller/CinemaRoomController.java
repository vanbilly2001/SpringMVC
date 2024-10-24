package com.example.MovieBooking.controller;

import com.example.MovieBooking.entity.CinemaRoom;
import com.example.MovieBooking.service.ICinemaRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.MovieBooking.entity.Seat;
import com.example.MovieBooking.service.ICinemaRoomService;
import com.example.MovieBooking.service.ISeatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cinemaRoom")
public class CinemaRoomController {
    @Autowired
    private ICinemaRoomService cinemaRoomService;

    @Autowired
    private ISeatService seatService;

    @GetMapping("/listCinemaRoom")
    public String listCinemaRoom(@RequestParam(value = "valueSearch", defaultValue = "", required = false) String valueSearch,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "5") int size,
                                 Model model) {
        String search = "";
        Page<CinemaRoom> listCinemaRoom;

        // Check valueSearch is valid
        if(valueSearch != null && !valueSearch.equals("")) {
            // Assign the search term to the 'search' variable.
            search = valueSearch;
            try{
                // Convert value from String data type to long
                Long valueSearhLong = Long.parseLong(search);
                // If the conversion is successful, get listCinemaRoom by valueSearchLong
                listCinemaRoom = cinemaRoomService.getCinemaRoomsByIdOrBySeatQuantity(valueSearhLong, page, size);
            } catch(NumberFormatException e){
                // If conversion fails , get listCinemaRoom by search.
                listCinemaRoom = cinemaRoomService.getCinemaRoomsByName(search, page, size);
            }
        } else {
            // If valueSearch = null or empty, get listCinemaRoom with search is empty
            listCinemaRoom = cinemaRoomService.getCinemaRoomsByName(search, page, size);
        }

        // Add attributes to the model for use in the view.
        model.addAttribute("listCinemaRoom", listCinemaRoom);
        model.addAttribute("totalPages", listCinemaRoom.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("search", search);

        return "cinemaRoom/listCinemaRoom";
    }

    @GetMapping("/formAddCinemaRoom")
    public String formAddCinemaRoom(Model model) {
        CinemaRoom cinemaRoom = new CinemaRoom();
        model.addAttribute("cinemaRoom", cinemaRoom);
        return "cinemaRoom/addCinemaRoom";
    }

    @PostMapping("/addCinemaRoom")
    public String addCinemaRoom(@Valid @ModelAttribute("cinemaRoom") CinemaRoom cinemaRoom, BindingResult result) {

        // Check for validation errors
        if (result.hasErrors()) {
            return "cinemaRoom/addCinemaRoom";
        }

        // Check if the cinema room name already exists in the database
        if (cinemaRoomService.existsByCinemaRoomName(cinemaRoom.getCinemaName())) {
            result.rejectValue("cinemaName", "error.cinemaRoom", "Cinema Room Name already exists.");
            return "cinemaRoom/addCinemaRoom";
        }

        // If there are no errors, save CinemaRoom.
        cinemaRoomService.saveCinemaRoom(cinemaRoom);
        return "redirect:/cinemaRoom/listCinemaRoom";
    }

    @GetMapping("/seatDetail")
    public String seatDetailPage(
            @RequestParam("cinemaRoomId") Long cinemaRoomId, 
            Model model) {

        // Get CinemaRoom after getting cinemaRoomId from param
        CinemaRoom cinemaRoom = cinemaRoomService.getCinemaRoomById(cinemaRoomId);

        // Get list of seats with same cinemaRoomId
        List<Seat> listSeat = seatService.listSeatByCinemaRoomId(cinemaRoomId);

        // Create a list to contain the rows of seats
        List<List<Seat>> rows = new ArrayList<>();

        // Divide the seat list into rows
        for (int i = 0; i < listSeat.size(); i += 6) {
            int end = Math.min(i + 6, listSeat.size());
            rows.add(listSeat.subList(i, end));
        }

        // Add attributes to the model for use in the view.
        model.addAttribute("seatRows", rows);
        model.addAttribute("nameCinemaRoom", cinemaRoom.getCinemaName());

        return "cinemaRoom/seatDetail";
    }

    @PostMapping("/updateSeatType")
    public String updateSeatType(@RequestParam("selectedSeat") Long[] selectedSeat,@RequestParam("seatType") String seatType) {

        // Call service to update the seat types
        seatService.updateListSeatType(selectedSeat, seatType);

        return "redirect:/cinemaRoom/listCinemaRoom";
    }

}

