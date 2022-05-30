<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
	<c:set var="path" value="${pageContext.request.contextPath}"/>
    <meta charset="UTF-8">
    <meta content="IE=edge">

	<link rel="manifest" href="${path}/resources/json/manifest.json">
    <!-- 모바일용웹 -->
	<meta name="viewport" content="initial-scale=1 width=device-width, height=device-height" />
	
	<!-- 안드로이드 홈화면추가시 상단 주소창 제거 -->
	<meta name="mobile-web-app-capable" content="yes">
	<!-- ios홈화면추가시 상단 주소창 제거 -->
	
    <script src="${path}/resources/js/jquery.js"></script>
    <script src="${path}/resources/js/jquery-ui.js"></script>
    <script src="${path}/resources/js/jquery-cookie.js"></script>
    <script src="${path}/resources/js/JSEncrypt.js"></script>
    <script src="${path}/resources/js/fullpage.js"></script>
        
    <script src="${path}/resources/js/main.js"></script>
    <script src="${path}/resources/js/article.js"></script>
    <script src="${path}/resources/js/problem.js"></script>

    <link rel="stylesheet" href="${path}/resources/css/main.css">
    <title>CBT 웹 서비스</title>
</head>
<body>
	<div id="blancetnoir" class="touchable"><span class="touchable">Made By </span><span style="color:#06d6a0" class="touchable">B</span><span class="touchable">lanc et </span><span style="color:#ee3f5c" class="touchable">N</span><span class="touchable">oir</span></div>
    <div id="fullpage">
        <div class="section">
            <div class="slide touchable" style="background-color:#f4f4f4">
            	<p></p>
            </div>
        </div>
        <div class="section">
            <div class="slide" style="background-color:#f4f4f4">
            	<div id="card_borad_wrapper">
            		<div id="card_board" class="touchable">
						<div id="move_button1" class="card touchable" style="top:10%; left:10%; transform:translate(-10%,-10%);">
							<img class="touchable" src="${path}/resources/image/mobile.svg">
							<p class="touchable">평가시험</p>
						</div>
						<div id="move_button2" class="card touchable" style="top:10%; left:90%; transform:translate(-90%,-10%)">
							<img class="touchable" src="${path}/resources/image/people.svg">
							<p class="touchable">커뮤니티</p>
						</div>
						<div id="move_button3" class="card touchable" style="top:90%; left:10%; transform:translate(-10%,-90%);">
							<img class="touchable" src="${path}/resources/image/person.svg">
							<p class="touchable">내 정보</p>
						</div>
						<div id="move_button4" class="card touchable" style="top:90%; left:90%; transform:translate(-90%,-90%);">
							<img class="touchable" src="${path}/resources/image/project.svg">
							<p class="touchable">고객지원</p>
						</div>
						<p style="position:absolute; top:110%; left:50%; transform:translate(-50%,-110%); text-align:center;">한 번의 클릭으로 간편하게!</p>
					</div>	
            	</div>
				
            </div>
        </div>
        <div class="section">
            <div class="slide" style="background-color:#f4f4f4">
            	<div class="intro_box">
            		<div class="card touchable return_button" style="top:50%; left:5%; transform:translate(-5%,-50%)">
						<img class="touchable" src="${path}/resources/image/mobile.svg">
						<p class="touchable">평가시험</p>
					</div>
					<p class="description touchable">자신의 성적을 평가해보세요.<br>총 800여개의 문제가 제공됩니다.</p>
            	</div>
            </div>
			<div class="slide" style="background-color:#f4f4f4;">
				<div id="problem_div">
					<div id="problem_form" class="touchable">
						<select id="category_id" class="touchable"></select>
						<div id="get_problems_button" class='touchable'>조회하기</div>
					</div>
				</div>
        	</div>        
        </div>
        <div class="section">
            <div class="slide" style="background-color:#f4f4f4">
            	<div class="intro_box">
            		<div class="card touchable return_button" style="top:50%; left:5%; transform:translate(-5%,-50%)">
						<img class="touchable" src="${path}/resources/image/people.svg">
						<p class="touchable">커뮤니티</p>
					</div>
					<p class="description touchable">많은 사람들과 소통하세요.<br>그리고 더 많은 지식을 공유하세요.</p>
            	</div>
            </div>
            <div class="slide" style="background-color:#f4f4f4">
            	<div id="article_form_wrapper">
            		<div id="list_article_form" class="touchable">
            			<div id="article_search_panel" class="touchable">
            				<select id="search_flag" class="touchable">
            					<option selected value="user_id" label="작성자 ID" class="touchable"/>
            					<option value="article_title" label="게시글 제목" class="touchable"/>
            					<option value="article_content" label="게시글 내용" class="touchable"/>
            				</select>
            				<input id="search_content" class="touchable" placeholder="검색하고 싶은 내용을 이곳에 적어주세요.">
            				<div id="search_article_button" class="touchable">검색하기</div>
            			</div>
            			<div id="article_list" class="touchable"></div>
            			<div id="add_article_button" class="touchable" onclick="setAddArticleForm();">게시글을 작성하고 싶어요.</div>
            		</div>
            		<div id="add_article_form" class="touchable">
            			<div class="subtitle touchable">게시글 제목</div>
            			<input id="article_title" placeholder="게시글 제목은 이곳에 적어주세요." class="touchable">
            			<div class="subtitle touchable">게시글 내용</div>
            			<textarea rows="" cols="" id="article_content" placeholder="게시글 내용은 이곳에 적어주세요." class="touchable"></textarea>
            			<div id="article_images" class="touchable"></div>
            			<div id="list_article_button" class="touchable" onclick="setListArticleForm();">게시글들을 다시 보여주세요.</div>
            		</div>
            	</div>
            	
            </div>
        </div>
        <div class="section">
            <div class="slide" style="background-color:#f4f4f4">
            	<div class="intro_box">
            		<div class="card touchable return_button" style="top:50%; left:5%; transform:translate(-5%,-50%)">
						<img class="touchable" src="${path}/resources/image/person.svg">
						<p class="touchable">내 정보</p>
					</div>
					<p class="description touchable">회원님의 정보가 궁금하신가요?<br>여기에 다양한 정보가 있습니다.</p>
            	</div>
            </div>
			<div class="slide" style="background-color:#f4f4f4">
				<p>내용</p>
        	</div>          
        </div>
        <div class="section">
            <div class="slide" style="background-color:#f4f4f4">
            	<div class="intro_box">
            		<div class="card touchable return_button" style="top:50%; left:5%; transform:translate(-5%,-50%)">
						<img class="touchable" src="${path}/resources/image/project.svg">
						<p class="touchable">고객지원</p>
					</div>
					<p class="description touchable">도움이 필요하신가요?<br>신속하게 문제를 해결해드립니다.</p>
            	</div>
            </div>
			<div class="slide" style="background-color:#f4f4f4">
				<p>내용</p>
        	</div>     
        </div>
    </div>
</body>
</html>