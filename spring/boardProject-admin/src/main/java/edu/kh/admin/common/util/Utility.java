package edu.kh.admin.common.util;

import java.security.SecureRandom;
import java.util.Random;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Utility {
	
	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 6;
    
    public static String generatePassword() {
    	
    	 SecureRandom random = new SecureRandom(); 
    	 // SecureRandom: 난수를 생성하기 위한 클래스로, 보안적으로 더 강력한 랜덤 값을 생성함.
    	 // 일반적인 Random보다 예측 가능성이 낮아, 민감한 데이터(예: 암호 생성)와 같은 곳에 적합함.
    	 
         StringBuilder randomCode = new StringBuilder(CODE_LENGTH); // 길이6을 초기 용량으로 갖는 StringBuilder 객체 생성

         for (int i = 0; i < CODE_LENGTH; i++) {
             int index = random.nextInt(CHARACTERS.length()); // CHARACTERS의 길이(62)
             			// random.nextInt(62)는 0부터 61 사이의 난수를 생성
     
             randomCode.append(CHARACTERS.charAt(index));
             // CHARACTERS 문자열의 index 위치에 있는 문자를 반환
             // ex) index가 0이면 'A', index가 61이면 '9'를 반환
             // 반환받은 값을 randomCode에 누적
         }

         return randomCode.toString(); // StringBuilder에 저장된 문자열을 String으로 변환하여 반환
    }

	
}
