package com.wcs.project.controller;

import com.wcs.project.dto.key.AsymmetricDto;
import com.wcs.project.service.KeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.NoSuchAlgorithmException;

// 키 생성을 담당
@Controller
@RequiredArgsConstructor
@RequestMapping("/key")
public class KeyController {
    // 비대칭키 : asymmetric (개인키 + 공개키)
    // 대칭키 : symmetric (비밀키)

    private final KeyService keyService;

    // 비대칭키 생성 폼으로 이동
    @GetMapping("/asymmetricForm")
    public String moveToAsymmetricForm() {
        return "redirect:/key/asymmetricForm";
    }

    // 전자서명에 필요한 비대칭키를 생성 (공개키 + 개인키)
    @PostMapping("/asymmetric")
    public ModelAndView makeAsymmetricKey(@ModelAttribute @Validated AsymmetricDto asymmetricDto) throws NoSuchAlgorithmException {
        boolean result = keyService.generateAsymmetricKey(asymmetricDto);

        if(result)
            return new ModelAndView("home");

        // bindingResult 추가하기
        // 에러 발생 시 해당 화면으로 돌아가 alert창 띄우기

    }

    // 대칭키 생성 폼으로 이동
    @GetMapping("/symmetricForm")
    public String moveToSymmetricForm() {
        return "redirect:/key/symmetricForm";
    }

    // 2. 대칭암호화에 필요한 비밀키를 생성

}
