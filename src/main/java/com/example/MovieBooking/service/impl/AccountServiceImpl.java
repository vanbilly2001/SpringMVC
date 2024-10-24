package com.example.MovieBooking.service.impl;

import com.example.MovieBooking.dto.req.AccountDTO;
import com.example.MovieBooking.entity.Account;
import com.example.MovieBooking.dto.req.AccountReq;
import com.example.MovieBooking.entity.Account;
import com.example.MovieBooking.entity.Member;
import com.example.MovieBooking.entity.Role;
import com.example.MovieBooking.repository.AccountRepository;
import com.example.MovieBooking.service.IAccountService;
import com.example.MovieBooking.service.IMemberService;
import com.example.MovieBooking.service.IRoleService;
import com.example.MovieBooking.service.IUploadImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import java.util.Optional;

@Service
public class AccountServiceImpl implements IAccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IMemberService memberService;

    @Autowired
    private IUploadImage uploadImage;

    /**
     * Register new account
     *
     * @author Hoang Thanh Tai
     * @param account information of user used for register account
     */
    @Override
    public void register(AccountReq account) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Role role = roleService.findRoleByName("MEMBER");
        Account account1 = Account.builder()
                .username(account.getUsername())
                .password(account.getPassword())
                .address(account.getAddress())
                .gender(account.getGender())
                .dateOfBirth(LocalDate.parse(account.getDateOfBirth(), formatter))
                .fullname(account.getFullname())
                .email(account.getEmail())
                .identityCard(account.getIdentityCard())
                .phoneNumber(account.getPhoneNumber())
                .status(1)
                .role(role)
                .registerDate(LocalDate.now())
                .build();

        accountRepository.save(account1);

        Member member = new Member(null, 0L, account1);
        memberService.saveMember(member);
    }

    /**
     * Find account information by username
     *
     * @author Hoang Thanh Tai
     * @param username username of account
     * @return information of an account
     */
    @Override
    public Account findUserByUsername(String username) {
        return accountRepository.findByUsername(username).orElse(null);
    }

    /**
     * Find account information by username
     *
     * @author Hoang Thanh Tai
     * @param username username of account
     * @return user detail
     * @throws UsernameNotFoundException if username not found in database
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> account = accountRepository.findByUsername(username);
        if (account.isPresent() && account.get().getStatus() == 1) {
            Account account1 = account.get();
            return Account.builder()
                    .username(account1.getUsername())
                    .password(account1.getPassword())
                    .role(account1.getRole())
                    .status(account1.getStatus())
                    .build();
        } else
            throw new UsernameNotFoundException("User not found");
    }

    /**
     * Find account information by ID
     *
     * @author Hoang Thanh Tai
     * @param id the identity of account
     * @return information of an account
     */
    @Override
    public Account findUserById(Long id) {
        return accountRepository.findById(id).orElse(null);
    }

    /**
     * Update account information
     *
     * @author Hoang Thanh Tai
     * @param account information of account
     * @param imageUrl file image received in multipart request
     * @throws IOException if file does not exist
     */
    @Override
    public void updateAccount(AccountReq account, MultipartFile imageUrl) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Account account1 = findUserById(account.getId());
        account1.setPassword(account.getPassword());
        account1.setFullname(account.getFullname());
        account1.setDateOfBirth(LocalDate.parse(account.getDateOfBirth(), formatter));
        account1.setGender(account.getGender());
        account1.setIdentityCard(account.getIdentityCard());
        account1.setPhoneNumber(account.getPhoneNumber());
        account1.setAddress(account.getAddress());
        account1.setEmail(account.getEmail());
        if (!imageUrl.isEmpty()) {
            account1.setImage(uploadImage.uploadImage(imageUrl));
        }
        accountRepository.save(account1);
    }

    @Override
    public AccountDTO getMemberById(Long id) {
        Object[] object =  accountRepository.findByMemberId(id);
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setMemberId(Long.parseLong(String.valueOf(object[0])));
        accountDTO.setFullname(object[2].toString());
        accountDTO.setIdentityCard(object[1].toString());
        accountDTO.setPhoneNumber(object[3].toString());
        accountDTO.setScore(Integer.valueOf(object[4].toString()));
        return accountDTO;
    }

    @Override
    public AccountDTO getMemberByIdentityCard(String identityCard) {
        Object[] object = accountRepository.findByIdentityCard(identityCard);
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setMemberId(Long.valueOf(object[0].toString()));
        accountDTO.setFullname(object[2].toString());
        accountDTO.setIdentityCard(object[1].toString());
        accountDTO.setPhoneNumber(object[3].toString());
        accountDTO.setScore(Integer.valueOf(object[4].toString()));
        return accountDTO;

    }

    /**
     * Find account information by member's ID
     *
     * @author Hoang Thanh Tai
     * @param memberId the ID of member
     * @return information of member's account
     */
    public Account findUserByMemberId(Long memberId) {
        return accountRepository.findAccountByMemberId(memberId);
    }

    public Optional<Account> getAccountByUserName(String userName){
        return accountRepository.findByUsername(userName);
    }
}
