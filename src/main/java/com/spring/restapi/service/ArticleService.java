package com.spring.restapi.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.spring.restapi.dao.ArticleDAO;
import com.spring.restapi.exception.article.NotFoundArticleException;
import com.spring.restapi.exception.article.NotMatchedUserIdException;

@Transactional(propagation=Propagation.REQUIRED,rollbackFor={
	Exception.class,
	NotMatchedUserIdException.class,
	NotFoundArticleException.class,
	IOException.class
})
@Service("articleService")
public class ArticleService {
	@Autowired
	private ArticleDAO articleDAO;
	private static final String IMAGE_BASE_PATH = "restapi_files"+File.separator+"article_images"+File.separator;
	
	private static String getExtension(MultipartFile mf) {
		String[] str = mf.getOriginalFilename().split("\\.");
		return str[str.length-1];
	}
	
	public void addArticle(String contextPath, MultipartRequest mRequest,HashMap param) throws Exception {		
		
		
				
		articleDAO.addArticle(param);
		
		Queue<HashMap> list = new LinkedList<HashMap>();
		List<MultipartFile> files = mRequest.getFiles("article_image_files");
		Iterator<MultipartFile> itor = files.iterator();
		
		while(itor.hasNext()) {		
			MultipartFile mf = itor.next();
			HashMap hm = new HashMap();
			hm.put("article_image_file", mf);
			hm.put("article_image_id", UUID.randomUUID().toString());
			hm.put("article_image_extension", getExtension(mf));
			
			list.add(hm);
		}
		
		param.put("article_images", list);
		
		if(!list.isEmpty()) {
			articleDAO.addArticleImages(param);
		}
			
		while(!list.isEmpty()) {
			HashMap hm = list.poll();
			MultipartFile  mf = (MultipartFile) hm.get("article_image_file");
			String article_image_id = (String) hm.get("article_image_id");
			String article_id = (String) param.get("article_id");
			File file = new File(contextPath+IMAGE_BASE_PATH+article_id+File.separator+article_image_id+"."+getExtension(mf));
			
			if(!file.exists()) {
				file.mkdirs();
			}
			
			mf.transferTo(file);
		}
	}
	
	public void modifyArticle(MultipartRequest mRequest,HttpServletRequest request, HashMap param) throws NotFoundArticleException, NotMatchedUserIdException, Exception{
		Iterator<String> itor = mRequest.getFileNames();

		//1. ???? ???????? ?????????? ????.
		HashMap article = articleDAO.getArticle(param);
		if(article==null) {
			throw new NotFoundArticleException();
		}
		
		//2. ???????? ?????? ?????? ?????? ????.
		if(!article.get("user_id").equals(param.get("user_id"))) {
			throw new NotMatchedUserIdException();
		}
		
		//3. ?????? ?????????? ?????? ????, ???? ????
		articleDAO.updateArticle(param);
		
		//4. ?????? ?????? ?????????? ???? ?????? ????, ?????? ???? ?????? ????????  ?????? ?????? ???? ???? ????.
		
		List<HashMap> add_file_ids =  new LinkedList<HashMap>();
		
		while(itor.hasNext()) {
			MultipartFile mf = mRequest.getFile(itor.next());
			
			HashMap hm = new HashMap();
			hm.put("article_image_file", mf);
			hm.put("article_image_id", UUID.randomUUID().toString());
			hm.put("article_image_extension", getExtension(mf));
			
			add_file_ids.add(hm);
		}
		
		if(!add_file_ids.isEmpty()) {
			param.put("add_file_ids", add_file_ids);
			articleDAO.insertArticleImages(param);
		}
		
		
		
		String[] str = request.getParameterValues("removed_file_ids");

		List<String> remove_file_ids =  new LinkedList<String>();
		
		if(str !=null) {
			for(String s : str) {
				remove_file_ids.add(s);
			}
		}
		
		List<String> remove_file_extensions =  new LinkedList<String>();
		str = request.getParameterValues("removed_file_extensions");
		
		if(str !=null) {
			for(String s : str) {
				remove_file_extensions.add(s);
			}
		}
		
		if(!remove_file_ids.isEmpty()) {
			param.put("remove_file_ids", remove_file_ids);
			articleDAO.deleteArticleImages(param);
		}

		//4. DB?????? ??????????, ???? ???????? ?????? ?????? ?????? ?????? ???? ???? ?? ???????? ???? ??????.
		
		String article_id = (String) param.get("article_id");
		String contextPath = (String) param.get("contextPath");
		
		for(HashMap hm : add_file_ids) {
			MultipartFile mf = (MultipartFile) hm.get("article_image_file");
			
			String article_image_id = (String) hm.get("article_image_id");
		
			mf.transferTo(new File(contextPath+IMAGE_BASE_PATH+article_id+File.separator+article_image_id+"."+getExtension(mf)));
		}
		
		Iterator<String> itor_id = remove_file_ids.iterator();
		Iterator<String> itor_extension = remove_file_extensions.iterator();
		
		while(itor_id.hasNext()&&itor_extension.hasNext()) {
			String id = itor_id.next();
			String extension = itor_extension.next();
			File file = new File(contextPath+IMAGE_BASE_PATH+article_id+File.separator+id+"."+extension);
			if(file.exists()) {
				file.delete();
			}
		}
	}
	
	public HashMap getArticles(HashMap param){
		HashMap result = new HashMap();
		result.put("articles_total", articleDAO.getArticlesTotal(param));
		result.put("articles", articleDAO.getArticles(param));
		return result;
	}
	
	public HashMap getArticle(HashMap param) throws NotFoundArticleException{
		HashMap result = new HashMap();
		HashMap article = articleDAO.getArticle(param);
		
		if(article == null) {
			throw new NotFoundArticleException();
		}
		
		articleDAO.increaseArticleView(param);
		article.put("article_images", articleDAO.getArticleImages(param));
		
		result.put("article", article);
		return result;
	}
	
	public void getArticleImage(HttpServletRequest request,HttpServletResponse response, HashMap param) throws IOException{
		int len;
		String path = request.getServletContext().getRealPath("") + IMAGE_BASE_PATH+param.get("article_id")+File.separator+param.get("article_image_id")+"."+param.get("article_image_extension");
		File file = new File(path);

		if(!file.exists()) {
			return;
		}else {
			response.setHeader("Cache-Control", "no-cache");
			response.addHeader("Content-disposition", "attachment; fileName="+param.get("article_image_id")+"."+param.get("article_image_extension"));
			
			OutputStream out = response.getOutputStream();
			FileInputStream in = new FileInputStream(file);
			
			byte[] buffer = new byte[1024*1024*10];
			
			while((len = in.read(buffer))!=-1) {
				out.write(buffer, 0, len);
			}
			
			in.close();
			out.close();
			return;
		}
	}
	
	public void deleteArticle(HashMap<String,String> param) throws NotMatchedUserIdException, NotFoundArticleException, IOException, Exception {
		//1. ???????? ?????????? ????.
		HashMap<String,String> article = articleDAO.getArticle(param);

		if(article == null) {
			throw new NotFoundArticleException();
		}
		
		//2. ???????? ?????? ?????? ?????? ????.
		if(!article.get("user_id").equals(param.get("user_id"))) {
			throw new NotMatchedUserIdException();
		}
		
		//3. ???? ???????? ???????? ???? ????, ?????????? ???? ???????? ????.
		//articleDAO.deleteAllComments(param);
		
		//4. ???? ???????? ????
		articleDAO.deleteArticle(param);
		
		//5. ???? ???????? ???????? ????.
		articleDAO.deleteAllArticleImages(param);
		
		//6. ???? ???????? ???? ?????? ???? ????.
		File folder = new File(param.get("contextPath")+IMAGE_BASE_PATH+File.separator+param.get("article_id"));
		if(folder.exists()) {
			FileUtils.deleteDirectory(folder);
		}
	}
}
