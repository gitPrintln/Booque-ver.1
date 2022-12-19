package site.book.project.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.User;
import site.book.project.dto.UserSecurityDto;
import site.book.project.repository.UserRepository;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MyPageController {
    
    private final UserRepository userRepository;
    
    
    // (하은) 마이페이지 연결
    @GetMapping("/myPage")
    public String myPage(@AuthenticationPrincipal UserSecurityDto u, Model model) {
        
        User user = userRepository.findById(u.getId()).get();
        model.addAttribute("user", user);
        
        
        return "/book/myPage";
    }
    
}
