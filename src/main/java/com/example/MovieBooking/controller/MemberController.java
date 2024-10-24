package com.example.MovieBooking.controller;

import com.example.MovieBooking.dto.req.AccountReq;
import com.example.MovieBooking.entity.Account;
import com.example.MovieBooking.entity.dto.MemberDTO;
import com.example.MovieBooking.service.IAccountService;
import com.example.MovieBooking.service.IMemberService;
import com.example.MovieBooking.util.AccountRegisterValidate;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Controller
@RequestMapping("member-management")
public class MemberController {
    @Autowired
    IMemberService memberService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private AccountRegisterValidate accountRegisterValidate;


    @GetMapping("/members")
    public String showMembers(@RequestParam(value = "searchInput", required = false, defaultValue = "") String searchInput
            , @RequestParam(required = false, defaultValue = "0") int page
            , @RequestParam(required = false, defaultValue = "10") int size, Model model) {
        String search = "";
        if(searchInput != null && !searchInput.isEmpty()) {
            search = searchInput;
        }
        List<MemberDTO> memberDTOList = memberService.getAllMembers(search, page, size);
        int totalPages = memberService.getTotalPage(search,page,size);
        model.addAttribute("members", memberDTOList);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("search", search);

        return "member-list";
    }

    @GetMapping("/member/{id}")
    public String memberDetail(@PathVariable("id") Long id, Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Account account1 = accountService.findUserByMemberId(id);
        AccountReq accountEdit = AccountReq.builder()
                .id(account1.getAccountId())
                .username(account1.getUsername())
                .password(account1.getPassword())
                .address(account1.getAddress())
                .dateOfBirth(account1.getDateOfBirth().format(formatter))
                .email(account1.getEmail())
                .fullname(account1.getFullname())
                .gender(account1.getGender())
                .identityCard(account1.getIdentityCard())
                .phoneNumber(account1.getPhoneNumber())
                .build();
        model.addAttribute("account", accountEdit);
        model.addAttribute("image", account1.getImage());
        return "edit-member";
    }

    @PostMapping("/edit")
    public String editMember(@Valid @ModelAttribute("account") AccountReq account, @RequestParam(value = "image", required = false) MultipartFile image, BindingResult bindingResult, Model model) throws IOException {
        accountRegisterValidate.validate(account, bindingResult);
        if(bindingResult.hasErrors()){
            return "edit-member";
        }
        accountService.updateAccount(account, image);
        Account account1 = accountService.findUserByUsername(account.getUsername());
        model.addAttribute("image", account1.getImage());
        model.addAttribute("success", "Update information successfully");
        return "edit-member";
    }
}
