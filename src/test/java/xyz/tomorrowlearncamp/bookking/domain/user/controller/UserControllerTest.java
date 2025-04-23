//package xyz.tomorrowlearncamp.bookking.domain.user.controller;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import xyz.tomorrowlearncamp.bookking.domain.user.service.UserService;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("dev")
//class UserControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Test
//    @WithMockUser(roles = "USER")
//    @DisplayName("회원_권한_변경_실패-ROLE_USER_권한으로_ADMIN_요청_시_Forbidden")
//    void updateUserRole_fail_forbidden_whenUserTriesToPromote() throws Exception {
//        String requestBody = """
//                    {
//                        "role": "ROLE_ADMIN"
//                    }
//                """;
//
//        mockMvc.perform(patch("/api/v1/users/1/role")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andExpect(status().isForbidden());
//    }
//}
