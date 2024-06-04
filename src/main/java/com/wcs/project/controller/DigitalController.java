package com.wcs.project.controller;

import com.wcs.project.dto.envelope.SignDto;
import com.wcs.project.dto.envelope.VerifyDto;
import com.wcs.project.service.DigitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/digital/envelope")
public class DigitalController {
    private final DigitalService digitalService;

    // 전자봉투 생성 폼으로 이동
    @GetMapping("/signForm")
    public String moveToSignForm() {
        return "/envelope/signForm";
    }

    // 전자봉투 생성
    @PostMapping("/sign")
    public ModelAndView digitalEnvelopeSign(@ModelAttribute @Validated SignDto request, BindingResult bindingResult) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        if(bindingResult.hasErrors()) {
            return new ModelAndView("/envelope/signForm", "errorMessage", "모든 필드값을 입력해주세요.");
        }

        try {
            digitalService.digitalEnvelopeSign(request);
            return new ModelAndView("home", "message", "전자봉투 생성에 성공했습니다.");
        } catch (Exception e) {
            return new ModelAndView("/envelope/signForm", "errorMessage", "전자봉투 생성에 실패했습니다.");
        }
    }

    // 전자봉투 개봉 폼으로 이동
    @GetMapping("/verifyForm")
    public String moveToVerifyForm() {
        return "/envelope/verifyForm";
    }

    // 전자봉투 개봉
    @PostMapping("/verify")
    public ModelAndView digitalEnvelopeVerify(@ModelAttribute @Validated VerifyDto.Request request, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return new ModelAndView("/envelope/verifyForm", "errorMessage", "모든 필드값을 입력해주세요.");
        }

        try {
            VerifyDto.Response response = digitalService.digitalEnvelopeVerify(request);
            return new ModelAndView("/envelope/verifyResult", "response", response);
        } catch (Exception e) {
            return new ModelAndView("/envelope/verifyForm", "errorMessage", "전자봉투 개봉에 실패했습니다.");
        }
    }
}
