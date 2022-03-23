package com.spring.restapi.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.spring.restapi.exception.article.FailedToAddArticleException;

@Repository("articleDAO")
public class ArticleDAO {
	@Autowired
	private SqlSession sqlSession;
	
	public void addArticle(HashMap<String,String> param) throws FailedToAddArticleException {
		if(sqlSession.insert("article.addArticle",param)==0) {
			throw new FailedToAddArticleException();
		}
	}
	
	public List listArticles(HashMap<String,String> param){
		return sqlSession.selectList("article.listArticles", param);
	}
}