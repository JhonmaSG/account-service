package com.finance.accountservice.transaction.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.accountservice.audit.service.AuditService;
import com.finance.accountservice.entity.Account;
import com.finance.accountservice.entity.AccountStatus;
import com.finance.accountservice.repository.AccountRepository;
import com.finance.accountservice.security.user.entity.Role;
import com.finance.accountservice.security.user.entity.UserEntity;
import com.finance.accountservice.security.user.repository.UserRepository;
import com.finance.accountservice.transaction.dto.request.CreateTransactionRequest;
import com.finance.accountservice.transaction.entity.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DisplayName("Transaction Integration Tests")
class TransactionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AuditService auditService;

    private String token;
    private Account account;
    private Account otherAccount;

    @BeforeEach
    void setup() {

        accountRepository.deleteAll();
        userRepository.deleteAll();

        UserEntity user = UserEntity.builder()
                .username("Rayito")
                .email("rayito@test.com")
                .password(passwordEncoder.encode("123456"))
                .firstName("Rayito")
                .lastName("McQueen")
                .role(Role.USER)
                .build();

        UserEntity savedUser =
                userRepository.save(user);

        UserEntity otherUser = UserEntity.builder()
                .username("Carlos")
                .email("carlos@test.com")
                .password(passwordEncoder.encode("123456"))
                .firstName("Carlos")
                .lastName("Pepito")
                .role(Role.USER)
                .build();

        UserEntity savedOtherUser =
                userRepository.save(otherUser);

        account = Account.builder()
                .accountNumber("ACC-123")
                .balance(BigDecimal.valueOf(100000))
                .status(AccountStatus.ACTIVE)
                .user(savedUser)
                .build();

        accountRepository.save(account);

        otherAccount = Account.builder()
                .accountNumber("ACC-999")
                .balance(BigDecimal.valueOf(500000))
                .status(AccountStatus.ACTIVE)
                .user(savedOtherUser)
                .build();

        accountRepository.save(otherAccount);
    }

    private void authenticate() throws Exception {

        Map<String, String> loginRequest =
                new HashMap<>();

        loginRequest.put("username", "Rayito");
        loginRequest.put("password", "123456");

        String response = mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                loginRequest
                                        )
                                )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode =
                objectMapper.readTree(response);

        token = jsonNode.get("token").asText();
    }

    @Test
    @DisplayName("Should create deposit transaction and increase account balance")
    void shouldCreateDepositTransaction() throws Exception {
        authenticate();

        CreateTransactionRequest request =
                buildTransactionRequest(
                        TransactionType.DEPOSIT,
                        BigDecimal.valueOf(50000),
                        "Ingreso"
                );

        mockMvc.perform(
                        post("/transactions")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type")
                .value("DEPOSIT"))

                .andExpect(jsonPath("$.amount")
                        .value(50000));

        Account updatedAccount =
                accountRepository.findById(account.getId())
                        .orElseThrow();
        assertEquals(
                0,
                BigDecimal.valueOf(150000)
                        .compareTo(updatedAccount.getBalance())
        );
    }

    @Test
    @DisplayName("Should return bad request when withdraw amount exceeds balance")
    void shouldReturnBadRequestWhenBalanceIsInsufficient() throws Exception {

        authenticate();

        CreateTransactionRequest request =
                buildTransactionRequest(
                        TransactionType.WITHDRAW,
                        BigDecimal.valueOf(999999),
                        "Retiro imposible"
                );

        mockMvc.perform(
                        post("/transactions")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Insufficient balance"));

        Account updatedAccount =
                accountRepository.findById(account.getId())
                        .orElseThrow();

        assertEquals(
                0,
                BigDecimal.valueOf(100000)
                        .compareTo(updatedAccount.getBalance())
        );
    }

    private CreateTransactionRequest buildTransactionRequest(
            TransactionType type,
            BigDecimal amount,
            String description
    ) {
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setAccountId(account.getId());
        request.setType(type);
        request.setAmount(amount);
        request.setDescription(description);

        return request;
    }

    @Test
    @DisplayName("Should return unauthorized when token is missing")
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {

        CreateTransactionRequest request =
                buildTransactionRequest(
                        TransactionType.DEPOSIT,
                        BigDecimal.valueOf(50000),
                        "Ingreso"
                );

        mockMvc.perform(
                        post("/transactions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return unauthorized when token is invalid")
    void shouldReturnUnauthorizedWhenTokenIsInvalid() throws Exception {

        CreateTransactionRequest request =
                buildTransactionRequest(
                        TransactionType.DEPOSIT,
                        BigDecimal.valueOf(50000),
                        "Ingreso"
                );

        mockMvc.perform(
                        post("/transactions")
                                .header(
                                        "Authorization",
                                        "Bearer invalid-token"
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should deny access when user tries to access another user's account")
    void shouldDenyAccessToAnotherUsersAccount() throws Exception {

        authenticate();

        CreateTransactionRequest request =
                new CreateTransactionRequest();

        request.setAccountId(otherAccount.getId());

        request.setType(TransactionType.DEPOSIT);

        request.setAmount(BigDecimal.valueOf(50000));

        request.setDescription("Hack attempt");

        mockMvc.perform(
                        post("/transactions")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}
