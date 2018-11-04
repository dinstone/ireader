package com.dinstone.ireader.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dinstone.ireader.Configuration;
import com.dinstone.ireader.domain.Article;
import com.dinstone.ireader.domain.Category;
import com.dinstone.ireader.domain.Part;

public class ArticleServiceTest {

	public static void main(String[] args) {
		try {
			extractDirectory(2669);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void extractDirectory(int start) throws Exception {
		ArticleService as = new ArticleService();
		Article article = new Article();
		article.href = "http://www.yi-see.com/art_20134_9599.html";
		article.category = new Category();
		article.category.href = "http://www.yi-see.com/artc_1.html";
		article.parts = as.extractDirectory(article);
		article.file = new File(new Configuration().getRepositoryDir() + "/20134/all.txt");
		File parentFile = article.file.getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}

		if (article.parts != null) {
			int length = article.parts.length;
			String articleFile = parentFile + "/" + start + "-" + article.parts[length - 1].index + ".txt";
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(articleFile, true), "utf-8"));
			for (Part part : article.parts) {
				if (part.getIndex() >= start) {
					System.out.println(part.getIndex() + " : " + part.getUrl());
					String content = extractContent(article, part);
					writer.write("第" + part.index + "节");
					writer.newLine();
					writer.write(content);
					writer.newLine();
					writer.flush();
				}
			}
			writer.close();
		}

	}

	private static String extractContent(Article article, Part part) throws Exception {
		String content = null;

		int tryCount = 1;
		while (true) {
			try {
				Document doc = Jsoup.connect(part.url).referrer(article.href).timeout(5000).get();
				Elements divs = doc.select("div.ART");

				StringBuilder builder = new StringBuilder();
				for (Element div : divs) {
					builder.append(div.html().replace("<br>", "\r\n"));
				}

				content = builder.toString();

				break;
			} catch (Exception e) {
				tryCount++;
				if (tryCount > 3) {
					throw e;
				}
			}
		}

//		BufferedWriter writer = null;
//		try {
//			File partFile = new File(article.file.getParentFile(), part.index + ".txt");
//			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(partFile), "utf-8"));
//			writer.write(content);
//			writer.flush();
//		} catch (Exception e) {
//		} finally {
//			if (writer != null) {
//				try {
//					writer.close();
//				} catch (IOException e) {
//				}
//			}
//		}

		return content;
	}

}
