package com.spring.restapi.interceptor;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.spring.restapi.util.CookieUtil;
import com.spring.restapi.util.JwtUtil;
import com.spring.restapi.util.RedisUtil;

import io.jsonwebtoken.ExpiredJwtException;

public class AccesstokenInterceptor implements HandlerInterceptor{
	
	//액세스 토큰이 필요한 기능을 요청할때, 해당 액세스 토큰이 유효하지 않은경우 에러 메세지를 전달함.
	private void setErrorMessage(HttpServletResponse response, int errorcode, String message){
		//1. JSON의 형태로 요청 성공 여부 및 메세지를 전달함.
		try {
			JSONObject json = new JSONObject();
			json.put("flag", false);
			json.put("content", message);
			
			response.setStatus(401);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json.toString());
		//2. 기타 예외가 발생한 경우.
		} catch (IOException e) {
			
		}
	}
	
	@Override
	//액세스 토큰이 필요한 기능을 호출하면, 해당 액세스 토큰이 유효성 여부를 판단하여 해당 요청을 컨트롤러로 전송할지 여부를 결정함.
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		String user_accesstoken = CookieUtil.getAccesstoken(request);
		String uri = request.getRequestURI();
		String method = request.getMethod();

		
		
		//1. 신규 회원가입 요청시에는 액세스 토큰이 필요없음.
		if(uri.equals("/restapi/users")&&method.equals("POST")) {
			return true;
		//2. 회원정보를 조회하는 요청 외의 모든 GET 요청은 액세스 토큰이 필요없음.
		}else if(!uri.equals("/restapi/users")&&method.equals("GET")) {
			return true;
		}
		
		//3. 액세스 토큰이 필요하나 액세스 토큰이 없는 경우에는, UNAUTHORIZED 응답.
		if(user_accesstoken==null) {
			setErrorMessage(response,401,"로그인 정보가 유효하지 않음");
			return false;
		//4. 액세스 토큰이 필요하고, 액세스 토큰이 있는 경우에는 아래의 처리로직을 수행함.
		}else {
			//5. Redis의 블랙리스트에 존재하는 액세스 토큰일 경우에는 정상적으로 로그아웃 처리된 토큰이므로 UNAUTHORIZED 응답.
			if(RedisUtil.getData(user_accesstoken)!=null) {
				setErrorMessage(response,401,"로그인 정보가 유효하지 않음");
				return false;
			}
			
			//6. 액세스 토큰이 위조되지 않았고, 아직 유효기간이 남아있는 경우 컨트롤러로 해당 요청을 전달함.
			try {
				JwtUtil.validateToken(user_accesstoken);
				return true;
			//7. 액세스 토큰의 유효기간이 지난 경우 UNAUTHORIZED 응답.
			}catch(ExpiredJwtException e) {
				setErrorMessage(response,401,"로그인 정보가 유효하지 않음");
				return false;
			//8. 액세스 토큰이 위조된 경우 UNAUTHORIZED 응답.
			}catch(Exception e) {
				setErrorMessage(response,401,"로그인 정보가 유효하지 않음");
				return false;
			}
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		// TODO Auto-generated method stub
		
	}
}