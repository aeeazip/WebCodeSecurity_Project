package com.wcs.project.controller;

import com.wcs.project.dto.key.AsymmetricDto;
import com.wcs.project.dto.key.SymmetricDto;
import com.wcs.project.service.KeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.NoSuchAlgorithmException;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/key")
public class KeyController {
    private final KeyService keyService;

    // 비대칭키 생성 폼으로 이동
    @GetMapping("/asymmetricForm")
    public String moveToAsymmetricForm() {
        return "/key/asymmetricForm";
    }

    // 전자서명에 필요한 비대칭키를 생성 (공개키 + 개인키)
    @PostMapping("/asymmetric")
    public ModelAndView generateAsymmetricKey(@ModelAttribute @Validated AsymmetricDto request,
                                          BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return new ModelAndView("key/asymmetricForm", "errorMessage", "모든 필드값을 입력해주세요.");
        }

        try {
            keyService.generateAsymmetricKey(request);
            return new ModelAndView("home", "message", "비대칭키 생성에 성공했습니다.");
        } catch (Exception e) {
            return new ModelAndView("key/asymmetricForm", "errorMessage", "비대칭키 생성에 실패했습니다.");
        }
    }

    // 대칭키 생성 폼으로 이동
    @GetMapping("/symmetricForm")
    public String moveToSymmetricForm() {
        return "/key/symmetricForm";
    }

    // 대칭암호화에 필요한 비밀키를 생성
    @PostMapping("/symmetric")
    public ModelAndView generateSymmetricKey(@ModelAttribute @Validated SymmetricDto request,
                                             BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return new ModelAndView("key/symmetricForm", "errorMessage", "모든 필드값을 입력해주세요.");
        }

        try {
            keyService.generateSymmetricKey(request);
            return new ModelAndView("home", "message", "비밀키 생성에 성공했습니다.");
        } catch (Exception e) {
            return new ModelAndView("key/symmetricForm", "errorMessage", "비밀키 생성에 실패했습니다.");
        }
    }
}
