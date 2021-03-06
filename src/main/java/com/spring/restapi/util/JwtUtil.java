package com.spring.restapi.util;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.spring.restapi.vo.UserVO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtil {
	//임시로 비밀키를 하드코딩하였으나 추후 변경예정
	private static String secretKey = Base64.getEncoder().encodeToString("대칭키".getBytes());
	public static final long refreshtokenMaxAge = 14*24*60*60*1000;
	//public static final long accesstokenMaxAge = 1*60*60*1000;
	public static final long accesstokenMaxAge = 10*1000;
	public static final int privateKeyMaxAge = 1*60*1000;
	
	//사용자 정보를 바탕으로 토큰을 생성함.
	public static String createToken(UserVO user, long age) {
		
		//1. JWT 토큰의 헤더 정보를 설정함.
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("typ", "JWT");
		headers.put("alg", "HS256");
		
		//2. 사용자 정보를 해당 토큰에 추가함. 단, 민감한 정보는 토큰에 추가하지 않도록 함
		Map<String,Object> claims = new HashMap<String,Object>();
		claims.put("user_id", user.getUser_id());
		
		//3. 토큰의 유효시간을 설정함
		Date now = new Date();
		Date exp = new Date(now.getTime()+age);

		//4. 설정된 헤더, 클레임 정보를 바탕으로 새로운 JWT 토큰을 생성함.
		return Jwts.builder()
				.setHeader(headers)
				.setClaims(claims)
				.setSubject("user-auth")
				.setIssuedAt(now)
				.setExpiration(exp)
				.signWith(SignatureAlgorithm.HS256, secretKey)
				.compact();
	}
	
	//클라이언트로부터 전달받은 해당 토큰이 위조되었는지 아닌지 판단함.
	public static void validateToken(String token){
		//1. 해당 JWT 토큰에 문제가 있는지 판단함.
		Claims claims = null;
		claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
	}
	
	//클라이언트로부터 전달받은 해당 토큰에 저장된 정보를 얻음.
    public static Map<String,Object> getInfo(String token) throws Exception{
    	//1. 해당 JWT 토큰으로부터 저장된 정보를 얻음.
    	try {
    		Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
    		return claims.getBody();
    	//2. 기타 예외가 발생한 경우.
    	}catch(Exception e) {
    		return null;
    	}   	
    }
    
    //클라이언트로부터 전달받은 해당 토큰에 저장된 정보를 얻음.
    public static String getData(String token, String data) throws Exception{
    	//1. 해당 JWT 토큰으로부터 저장된 정보를 얻음.
    	try {
    		Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
    		return (String) claims.getBody().get(data);
    	//2. 해당 JWT 토큰의 유효기간이 지났어도 저장된 정보를 얻음.
    	}catch(ExpiredJwtException e) {
    		return (String) e.getClaims().get(data);
    	//3. 기타 예외가 발생한 경우.
    	}catch(Exception e) {
    		return null;
    	}
    }
    
    //클라이언트로부터 전달받은 해당 토큰의 남은 유효시간을 얻음.
    public static Long getExpiration(String token) {
    	//1. 해당 토큰의 남은 유효시간을 얻음.
    	try {
            Date expiration = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration();
            Long now = new Date().getTime();
            return (expiration.getTime() - now);
        //2. 기타 예외가 발생한 경우.
    	}catch(Exception e) {
    		return 1L;
    	}
    }
}